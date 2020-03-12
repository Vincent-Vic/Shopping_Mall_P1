package cn.iflags.Mall.service;

import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.pojo.UserInfo;
import cn.iflags.Mall.vo.UserVo;

/**
 * 描述:用户服务
 * @author Vincent Vic
 * create 2020-02-14 17:26
 */


public interface IUserService {

    //登录接口
    ServerResponse<User> login(String username, String password);
    //注册接口
    public ServerResponse<String> register(User user);
    //校验数据（用户/邮箱）
    public ServerResponse<String> checkValid(String str , String type);
    //通过名字获取问题
    public ServerResponse<String> selectQuestion(String question);
    //校验问题答案
    public ServerResponse<String> checkAnswer(String username,String question,String answer);
    //忘记密码提交重置密码
    public ServerResponse<String> forgetRestPasword(String username,String passwordNew,String forgetToken);
    //重置密码
    public ServerResponse<String> restPasword(User user,String passwordOld,String passwordNew);
    //更新用户账号信息
    public ServerResponse<User> updataAccountInformation(User user);
    //更新用户资料信息
    public ServerResponse<UserInfo> updataDataInformation(UserInfo userInfo);
    //get得到登入信息
    public ServerResponse<UserVo> getInformation(Integer userId);
    //校验是否是管理员
    public ServerResponse checkAdminRole(User user);
}