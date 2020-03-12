package cn.iflags.Mall.controller.portal;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.Shipping;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.service.IShippingService;
import cn.iflags.Mall.vo.CartVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 描述:收货地址的功能模块
 * @author Vincent Vic
 * create 2020-02-21 13:18
 */

@Controller
@RequestMapping("/shipping/")   //请求地址设置
public class ShippingController {

    @Autowired
    IShippingService iShippingService;

    // 增
    // 通过类，作为对象绑定接口
    @RequestMapping(value = "add.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse add(HttpSession session, Shipping shipping){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        return iShippingService.add(user.getId(),shipping);
    }

    // 删
    @RequestMapping(value = "del.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse deleteShipping(HttpSession session,  Integer shippingId){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        return iShippingService.deleteShipping(user.getId(),shippingId);
    }

    // 改
    @RequestMapping(value = "update.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse update(HttpSession session, Shipping shipping){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        return iShippingService.update(user.getId(),shipping);
    }

    // 查
    @RequestMapping(value = "select.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<Shipping> select(HttpSession session,Integer shippingId){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        return iShippingService.select(user.getId(),shippingId);
    }

    // 查
    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum, @RequestParam(value = "pageSize" ,defaultValue = "10") int pageSize){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        return iShippingService.list(user.getId(),pageNum,pageSize);
    }


}