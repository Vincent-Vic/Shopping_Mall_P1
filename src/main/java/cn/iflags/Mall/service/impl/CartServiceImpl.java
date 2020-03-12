package cn.iflags.Mall.service.impl;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.ResponseCode;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.dao.CartMapper;
import cn.iflags.Mall.dao.ProductMapper;
import cn.iflags.Mall.pojo.Cart;
import cn.iflags.Mall.pojo.Product;
import cn.iflags.Mall.service.ICartService;
import cn.iflags.Mall.util.BigDecimalUtil;
import cn.iflags.Mall.util.PropertiesUtil;
import cn.iflags.Mall.vo.CartProductVo;
import cn.iflags.Mall.vo.CartVo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * 描述:
 *
 * @author Vincent Vic
 * create 2020-02-20 10:55
 */

@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    //得到购物车列表
    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    //添加购物车商品
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //重载购物车里面是否已经存在
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if (cart == null){
            //产品不在购物车中需要新增一个
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);    //设置购物车数量
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            //加入购物车数据
            cartMapper.insert(cartItem);
        }else {//已存在 增加购物车数量
            count += cart.getQuantity();
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    //更新购物车商品
    public ServerResponse<CartVo> updata(Integer userId,Integer productId,Integer count){
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //重载购物车里面是否已经存在
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if (cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    //删除商品
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds){
        //将字符串转换成list 通过逗号分割
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productIdList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productIdList);
        return this.list(userId);
    }


    //选/取消选
    public ServerResponse<CartVo> selectOrUnselect(Integer userId,Integer productId,Integer checked){
        cartMapper.checkedOrUncheckedProduct(userId,productId,checked);
        return this.list(userId);
    }

    //购物车产品数量
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if (userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    //购物车信息
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        //数据库查找用户的购物车里面的数据
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        //将cartList 放入CartProduct的列表
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        //购物车总价
        BigDecimal cartTotal = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(cartList)){
            for (Cart cartItem : cartList){
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                CartProductVo cartProductVo;
                if (product == null){
                    cartProductVo = new CartProductVo(cartItem, product);
                }else {
                    cartProductVo = new CartProductVo(cartItem, product);
                    int buyLimitCount = 0;
                    if (product.getStock() >= cartItem.getQuantity()) {
                        //库存充足
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    //设置购物车单个商品数量
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity().doubleValue()));
                }
                //计算机整个购物车总价
                if (cartItem.getChecked() == Const.Cart.CHECKED){

                    cartTotal = BigDecimalUtil.add(cartTotal.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }

        }
        cartVo.setCartTotalPrice(cartTotal);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    //查询商品所有是否都选中
    private boolean getAllCheckedStatus(Integer userId){
        if (userId == null) return false;
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }


}