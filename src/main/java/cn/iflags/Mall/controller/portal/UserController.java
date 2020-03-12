package cn.iflags.Mall.controller.portal;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.ResponseCode;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.pojo.UserInfo;
import cn.iflags.Mall.service.IUserService;
import cn.iflags.Mall.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 描述: 前端用户控制
 * @author Vincent Vic
 * create 2020-02-14 17:03
 */

@Controller
@RequestMapping("/user/")   //请求地址设置
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @param session 会话
     * @return 上层数据
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public Object login(String username , String password, HttpSession session){
        //server->mybatis->dao
        ServerResponse<User> response = iUserService.login(username,password);

        if(response.isSeccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 登出
     * @param session 会话
     * @return 上层数据
     */
    @RequestMapping(value = "logou.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public Object logou(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 注册功能
     * @param user 用户信息
     * @return 上层数据
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 校验用户名/邮箱数据
     * @param str 用户名/邮箱数据
     * @param type 用户名/邮箱类型
     * @return 上层数据
     */
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * 获取用户信息
     * @param session 会话
     * @return 上层数据/用户未登录
     */
    @RequestMapping(value = "get_user_Info.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public Object getUserInfo(HttpSession session){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户为登入，无法获取用户信息");
    }

    /**
     * 忘记密码之返回问题功能
     * @param username 问题
     * @return 上层数据
     */
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    /**
     * 忘记密码之校验密码
     * @param username 用户姓名
     * @param question 问题
     * @param answer 答案
     * @return 上层数据
     */
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    /**
     * 忘记密码之重置密码
     * @param username 用户姓名
     * @param passwordNew 新密码
     * @param forgetToken token
     * @return 上层数据
     */
    @RequestMapping(value = "forget_rest_pasword.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<String> forgetRestPasword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetRestPasword(username, passwordNew, forgetToken);
    }

    /**
     * 重置密码
     * @param session 会话
     * @param passwordOld 旧密码
     * @param passwordNew   新密码
     * @return 上层数据
     */
    @RequestMapping(value = "rest_pasword.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<String> RestPasword(HttpSession session,String passwordOld,String passwordNew){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.restPasword(user, passwordOld,passwordNew);
    }

    /**
     * 更新账户信息
     * @param session 登录信息
     * @param user 更新用户信息
     * @return 未登录/成功/失败
     */
    @RequestMapping(value = "updata_account_info.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<String> updata_account_information(HttpSession session,User user){
        //获取当前用户
        User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentuser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentuser.getId());
        ServerResponse<User> response = iUserService.updataAccountInformation(user);
        if (response.isSeccess()){
            return ServerResponse.createBySuccessMessage("更新成功");
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }

    /**
     * 更新信息
     * @param session 登录信息
     * @param userInfo 更新用户信息
     * @return 未登录/成功/失败
     */
    @RequestMapping(value = "updata_data_info.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<String> updata_data_information(HttpSession session, UserInfo userInfo){
        //获取当前用户
        User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentuser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        userInfo.setUserId(currentuser.getId());
        ServerResponse<UserInfo> response = iUserService.updataDataInformation(userInfo);
        if (response.isSeccess()){
            return ServerResponse.createBySuccessMessage("更新成功");
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }

    /**
     * 获取登录信息
     * @param session 会话
     * @return 未登录/登录信息/用户不存在
     */
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<UserVo> get_information(HttpSession session){
        //获取当前用户
        User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentuser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }

        return iUserService.getInformation(currentuser.getId());
    }
}