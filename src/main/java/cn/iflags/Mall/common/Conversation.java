package cn.iflags.Mall.common;

import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;

/**
 * 描述:用于判断系统是否及权限
 * @author Vincent Vic
 * create 2020-02-20 10:28
 */


public class Conversation {

    @Autowired
    private static IUserService iUserService;

    public static ServerResponse checkAdminRole(HttpSession session) {
        //获取当前用户
        User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentuser == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        if ( currentuser.getRole() != Const.Role.ROLE_ADMIN)//不是管理员
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_AUTHORITY.getCode(),ResponseCode.NO_AUTHORITY.getDesc());
        return ServerResponse.createBySuccessMessage("身份确认");
    }

}