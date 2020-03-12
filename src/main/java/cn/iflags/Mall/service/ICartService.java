package cn.iflags.Mall.service;

import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.vo.CartVo;

/**
 * 描述:
 *
 * @author Vincent Vic
 * create 2020-02-20 10:54
 */

public interface ICartService {

    //得到购物车列表
    public ServerResponse<CartVo> list(Integer userId);
    //购物车添加sp
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer conut);
    //更新购物车信息
    public ServerResponse<CartVo> updata(Integer userId,Integer productId,Integer count);
    //删除购物车商品
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds);
    //全选/取消全选
    public ServerResponse<CartVo> selectOrUnselect(Integer userId,Integer productId,Integer checked);
    //购物车总数
    public ServerResponse<Integer> getCartProductCount(Integer userId);

}
