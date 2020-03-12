package cn.iflags.Mall.controller.backend;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.Conversation;
import cn.iflags.Mall.common.ResponseCode;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 描述:后台管理订单接口
 * @author Vincent Vic
 * create 2020-02-23 19:44
 */

@Controller
@RequestMapping("/manage/order/")   //请求地址设置
public class OrderManageController {

    @Autowired
    private IOrderService iOrderService;

    //获取订单列表
    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
        ServerResponse response = Conversation.checkAdminRole(session);
        if (!response.isSeccess()){
            return response;
        }
        return iOrderService.manageList(pageNum,pageSize);
    }

    //获取订单列表
    @RequestMapping(value = "detail.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse orderDetail(HttpSession session,String orderNo) {
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse response = Conversation.checkAdminRole(session);
        if (!response.isSeccess()){
            return response;
        }
        return iOrderService.manageDetail(orderNo);
    }

    //获取订单列表
    @RequestMapping(value = "search.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse orderSearch(HttpSession session,String orderNo,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse response = Conversation.checkAdminRole(session);
        if (!response.isSeccess()){
            return response;
        }
        return iOrderService.manageSearch(orderNo,pageNum,pageSize);
    }

    //发货模块
    @RequestMapping(value = "send_goods.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<String> orderSendGoods(HttpSession session,String orderNo,@RequestParam(value = "ExpressOrder",defaultValue = "null") String ExpressOrder,  @RequestParam(value = "ExpressType",defaultValue = "null") String ExpressType) {
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse response = Conversation.checkAdminRole(session);
        if (!response.isSeccess()){
            return response;
        }
        return iOrderService.manageSendGoods(orderNo,ExpressOrder,ExpressType);
    }


}