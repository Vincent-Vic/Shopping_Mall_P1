package cn.iflags.Mall.controller.portal;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.Conversation;
import cn.iflags.Mall.common.ResponseCode;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.service.IOrderService;
import cn.iflags.Mall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 描述:订单模块控制接口（提供前台的）
 * @author Vincent Vic
 * create 2020-02-22 15:52
 */

@Controller
@RequestMapping("/order/")   //请求地址设置
public class OrderController {

    @Autowired
    IOrderService iOrderService;

    //立即购买创建订单 通过商品
    @RequestMapping(value = "createbyproduct.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse createByCart(HttpSession session, Integer productId,Integer quantity,Integer shippingId) {
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrderByProduct(user.getId(),productId,quantity,shippingId);
    }

    //从购物车创建订单
    @RequestMapping(value = "createbycart.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse createByCart(HttpSession session, Integer shippingId) {
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrderByCart(user.getId(),shippingId);
    }

    //取消订单
    @RequestMapping(value = "cancel.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse cancel(HttpSession session, String orderNo) {
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cencalOrder(user.getId(),orderNo);
    }

    //获取购物车商品
    @RequestMapping(value = "get_order_cart_product.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse getOrderCartProduct(HttpSession session) {
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    //获取订单详情
    @RequestMapping(value = "detail.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse getDetail(HttpSession session, String orderNo) {
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    //获取订单列表
    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse list(HttpSession session,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
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
        return iOrderService.Search(user.getId(),orderNo,pageNum,pageSize);
    }



}