package cn.iflags.Mall.controller.portal;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.ResponseCode;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.service.IPayService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 描述:支付接口
 * @author Vincent Vic
 * create 2020-02-26 16:47
 */

@Controller
@RequestMapping("/pay/")   //请求地址设置
public class PayController {

    @Autowired
    private IPayService iPayService;

    @RequestMapping(value = "alipay.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse aliPay(HttpSession session, String orderNo){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String suffix  = "pay_qCode";
        return iPayService.aliPay(orderNo,user.getId(),suffix);
    }

    @RequestMapping(value = "walletPay.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse walletPay(HttpSession session, String orderNo,String password){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iPayService.walletPay(user.getId(),orderNo,password);
    }

    @RequestMapping(value = "alipay_callback.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public Object alipay_callback(HttpServletRequest request){
        return iPayService.alipay_callback(request);
    }

    @RequestMapping(value = "query_order_pay_status.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, String orderNo){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse response =  iPayService.queryOrderPayStatus(user.getId(),orderNo);
        if (response.isSeccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}