package cn.iflags.Mall.controller.backend;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 描述:后台登录的用户
 * @author Vincent Vic
 * create 2020-02-15 12:53
 */

@Controller
@RequestMapping("/manage/user/")   //请求地址设置
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    //登录
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<User> login(String username , String passwoed, HttpSession session){
        //server->mybatis->dao
        ServerResponse<User> response = iUserService.login(username,passwoed);

        if(response.isSeccess()){
            User user = response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN){   //管理员登录
                session.setAttribute(Const.CURRENT_USER,user);
                return response;
            } else {
                return ServerResponse.createByErrorMessage("登录权限不足");
            }
        }
        return response;
    }
}