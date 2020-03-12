package cn.iflags.Mall.controller.portal;

/**
 * 描述:钱包接口类
 * @author Vincent Vic
 * create 2020-02-27 18:26
 */

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.ResponseCode;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.service.IWalletService;
import cn.iflags.Mall.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

/**
 * 信息查询
 * 充值
 * 提现-后期实现
 * 修改密码（回答密保问题作为确认）
 */

@Controller
@RequestMapping("/wallet/")   //请求地址设置
public class WalletControl {

    @Autowired
    private IWalletService iWalletService;


    //查询钱包信息
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse get_information(HttpSession session){
        //获取当前用户
        User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentuser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }
        return iWalletService.getInformation(currentuser.getId());
    }

    //充值钱包-实际操作直接移交支付服务类完成
    @RequestMapping(value = "recharge.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse recharge(HttpSession session, BigDecimal price){
        //获取当前用户
        User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentuser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }
        String suffix  = "pay_qCode";
        return iWalletService.recharge(currentuser.getId(),price,suffix);
    }

    //重置支付密码-忘记密码
    //回答密保获取token
    @RequestMapping(value = "forget_pay_pasword.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<String> forgetRestPasword(HttpSession session,String passwordNew,String forgetToken){
        //获取当前用户
        User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentuser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }
        return iWalletService.forgetRestPasword(currentuser.getId(), passwordNew, forgetToken);
    }

    //重置支付密码-忘记密码
    //使用旧密码
    @RequestMapping(value = "rest_pay_pasword.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<String>restPasword(HttpSession session,String passwordOld,String passwordNew){
        //获取当前用户
        User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentuser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }
        return iWalletService.restPasword(currentuser.getId(), passwordOld, passwordNew);
    }

}