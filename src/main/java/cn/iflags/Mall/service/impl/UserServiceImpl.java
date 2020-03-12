package cn.iflags.Mall.service.impl;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.common.TokenChache;
import cn.iflags.Mall.dao.UserInfoMapper;
import cn.iflags.Mall.dao.UserMapper;
import cn.iflags.Mall.dao.UserWalletMapper;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.pojo.UserInfo;
import cn.iflags.Mall.pojo.UserWallet;
import cn.iflags.Mall.service.IUserService;
import cn.iflags.Mall.service.IWalletService;
import cn.iflags.Mall.util.BigDecimalUtil;
import cn.iflags.Mall.util.DateTimeUtil;
import cn.iflags.Mall.util.MD5Util;
import cn.iflags.Mall.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * 描述:IUserService用户服务接口实现
 * @author Vincent Vic
 * create 2020-02-14 17:53
 */

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private IWalletService iWalletService;

    @Autowired
    private UserWalletMapper userWalletMapper;

    /**
     * 登入功能
     * @param username 用户名
     * @param password 密码
     * @return 存在/成功/错误
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        //判断用户存不存在
        int resultCount = userMapper.checkUserName(username);
        if (resultCount == 0) { //不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        //MD5密码加密
        String md5password= MD5Util.MD5EncodeUtf8(password);
        //校验密码
        User user = userMapper.selectLogin(username, md5password);
        if (user == null) { //密码错误
            return ServerResponse.createByErrorMessage("密码错误");
        }
        //密码置为空，防止密码泄露
        user.setPassword(StringUtils.EMPTY);
        //密保问题置为空，平常用不到
        user.setQuestion(StringUtils.EMPTY);
        //密保答案置为空，防止答案泄露
        user.setAnswer(StringUtils.EMPTY);
        //设置最后一次登录记录
        UserWallet userWallet = userWalletMapper.selectByUserId(user.getId());
        Date date =new Date();
        if (DateTimeUtil.dateToStr(userWallet.getLastLogin(),"yyyyMMdd")
                .compareTo(DateTimeUtil.dateToStr(date,"yyyyMMdd")) != 0){
            userWallet.setLastLogin(date);
            //后续改成服务类，接管登入加金币的操作
            userWallet.setCoin(BigDecimalUtil.add(userWallet.getCoin().doubleValue(),new Double(10).doubleValue()));
        }
        userWalletMapper.updateByPrimaryKeySelective(userWallet);

        return ServerResponse.createBySuccess("登入成功", user);
    }

    /**
     * 注册功能
     * @param user  用户信息
     * @return 存在/成功/失败
     */
    @Override
    public ServerResponse<String> register(User user) {

        //判断用户存在
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if (!validResponse.isSeccess()) { //校验提供
            return validResponse;
        }
        //判断邮箱是否存在
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if (!validResponse.isSeccess()) { //校验提供
            return validResponse;
        }
        //设置普通用户
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //插入用户
        int resultCount = userMapper.insert(user);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        //初始化钱包，默认支付密码123456
        Integer userid = userMapper.selectIDByUsername(user.getUsername());
        iWalletService.Initialization(userid);

        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 校验用户/邮箱是否存在
     * @param str  数据
     * @param type  类型
     * @return 存在/成功/参数错误
     */
    @Override
    public ServerResponse<String> checkValid(String str , String type){
        int resultCount = 0 ;
        if (org.apache.commons.lang3.StringUtils.isNotBlank(type)){

            //开始校验
            if (Const.USERNAME.equals(type)){
                //判断用户存在
                resultCount = userMapper.checkUserName(str);
                if (resultCount > 0) { //存在
                    return ServerResponse.createByErrorMessage("用户已存在");
                }
            }
            if (Const.EMAIL.equals(type)){
                //判断邮箱是否存在
                resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) { //存在
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 通过名字获取问题
     * @param  username  用户名
     * @return 不存在/密码为空/问题
     */
    @Override
    public ServerResponse<String> selectQuestion(String username){
        //判断用户存在
        ServerResponse questionResponse = this.checkValid( username,Const.USERNAME);
        if (questionResponse.isSeccess()) { //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String questionRe =  userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(questionRe)){
            return ServerResponse.createBySuccess(questionRe);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题为空");
    }


    /**
     *  校验答案
     * @param username 用户名
     * @param question 找回问题
     * @param answer  答案
     * @return 修改密码依据/不存在/错误
     */
    @Override
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        //判断用户存在
        ServerResponse questionResponse = this.checkValid(username,Const.USERNAME);
        if (questionResponse.isSeccess()) { //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        int ResultCount = userMapper.checkAnswer(username,question,answer);
        if (ResultCount > 0) {//说明用户存在，并且正确
            //生成随机uuid
            String forgetToken = UUID.randomUUID().toString();
            //设置时效缓存
            TokenChache.setKey(TokenChache.getTokenFprefix()+username,forgetToken);
            //返回uuid，作为修改提交的校验
            return ServerResponse.createBySuccessMessage(forgetToken);
        }
        return  ServerResponse.createByErrorMessage("问题答案错误");
    }

    /**
     *
     * @param username  用户名
     * @param passwordNew 新用户
     * @param forgetToken 校验token
     * @return 错误/不存在/过期/成功/失败
     */
    @Override
    public ServerResponse<String> forgetRestPasword(String username, String passwordNew, String forgetToken) {
        if (org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误：token需要传递");
        }
        //判断用户存在
        ServerResponse questionResponse = this.checkValid(username,Const.USERNAME);
        if (questionResponse.isSeccess()) { //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenChache.getKey(TokenChache.getTokenFprefix()+username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if(StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int reslutCount = userMapper.updataPasswordByUsername(username,md5Password);
            if (reslutCount > 0){
                return ServerResponse.createBySuccessMessage("修改成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token错误，错误请重新重置密码");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 重置密码
     * @param user 用户信息
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @return 旧不正确/修改成功/修改失败
     */
    @Override
    public ServerResponse<String> restPasword(User user, String passwordOld, String passwordNew) {
        //校验用户旧密码，确定是这个用户
        int resulCount = userMapper.cheackPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if (resulCount == 0){
            return ServerResponse.createByErrorMessage("旧密码不正确");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updataCont = userMapper.updateByPrimaryKeySelective(user);
        if (updataCont == 0 ){
            return ServerResponse.createByErrorMessage("修改失败");
        }
        return ServerResponse.createByErrorMessage("修改成功");
    }

    /**
     * 更新用户账号信息
     * @param user  用户信息
     * @return 邮箱存在/修改失败 /成功
     */
    @Override
    public ServerResponse<User> updataAccountInformation(User user) {
        //email不能已经存在
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 1) {
            return ServerResponse.createBySuccessMessage("邮箱已经被其他用户绑定");
        }
        //创建更新用户
        User updataUser = new User(user, false);

        resultCount = userMapper.updateByPrimaryKeySelective(updataUser);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("修改失败");
        }

        return ServerResponse.createBySuccessMessage("修改成功");
    }

    /**
     * 更新用户资料信息
     * @param userInfo  用户信息
     * @return 邮箱存在/修改失败 /成功
     */
    @Override
    public ServerResponse<UserInfo> updataDataInformation(UserInfo userInfo) {
        UserInfo selectUserInfo = userInfoMapper.selectByUserId(userInfo.getUserId());
        if (selectUserInfo != null){    //如果用户扩展信息表这个用户信存在更新
            userInfo.setId(selectUserInfo.getId());
            userInfoMapper.updateByPrimaryKeySelective(userInfo);
        } else {  //如果用户扩展信息表这个用户信息是空的，添加
            userInfoMapper.insert(userInfo);
        }
        return ServerResponse.createBySuccessMessage("修改成功");
    }

    /**
     * 获取用户信息
     * @param userId 用户id
     * @return 不存在/用户信息
     */
    @Override
    public ServerResponse<UserVo> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        return ServerResponse.createBySuccess(new UserVo(user,userInfo));
    }

    /**
     * 校验是否是管理员
     * @param user 校验用户
     * @return 状态码
     */
    @Override
    public ServerResponse checkAdminRole(User user){
        if (user !=null && user.getRole() == Const.Role.ROLE_ADMIN){   //管理员登录
            return ServerResponse.createBySuccess();
        } else {
            return ServerResponse.createByError();
        }
    }

}