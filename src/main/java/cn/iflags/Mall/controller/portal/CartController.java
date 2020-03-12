package cn.iflags.Mall.controller.portal;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.Conversation;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.service.ICartService;
import cn.iflags.Mall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 描述:购物车模块
 * @author Vincent Vic
 * create 2020-02-20 10:07
 */

@Controller
@RequestMapping("/cart/")   //请求地址设置
public class CartController {

    @Autowired
    ICartService iCartService;

    //添加购物车
    @RequestMapping(value = "add.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        return iCartService.add(user.getId(),productId,count);
    }

    //查询购物车
    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<CartVo> list(HttpSession session){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iCartService.list(user.getId());
    }

    //更新购物车
    @RequestMapping(value = "update.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        return iCartService.updata(user.getId(),productId,count);
    }

    //删除购物车商品
    //productId 通过逗号分隔，删除多个商品
    @RequestMapping(value = "detele_product.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<CartVo> deteleProduct(HttpSession session, String productIds){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    //购物车全选
    @RequestMapping(value = "select_all.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<CartVo> selectAll(HttpSession session){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iCartService.selectOrUnselect(user.getId(),null,Const.Cart.CHECKED);
    }
    //购物车取消全选
    @RequestMapping(value = "un_select_all.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<CartVo> unSelectAll(HttpSession session){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iCartService.selectOrUnselect(user.getId(),null,Const.Cart.UN_CHECKED);
    }
    //购物车商品选择
    @RequestMapping(value = "select.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<CartVo> Select(HttpSession session,Integer productId){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iCartService.selectOrUnselect(user.getId(),productId,Const.Cart.CHECKED);
    }
    //购物车产品取消选择
    @RequestMapping(value = "un_select.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<CartVo> unSelect(HttpSession session,Integer productId){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iCartService.selectOrUnselect(user.getId(),productId,Const.Cart.UN_CHECKED);
    }
    //查询购物车商品数量
    @RequestMapping(value = "get_cart_product_count.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        //获取当前用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }

}