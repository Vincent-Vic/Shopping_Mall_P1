package cn.iflags.Mall.service;

import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.vo.OrderVo;
import com.github.pagehelper.PageInfo;

/**
 * 描述:订单服务接口
 * @author Vincent Vic
 * create 2020-02-22 15:53
 */


public interface IOrderService {

    //通过单个商品创建订单
    public ServerResponse createOrderByProduct(Integer userId, Integer productId,Integer quantity,Integer shippingId);
    //通过购物车创建订单
    public ServerResponse createOrderByCart(Integer userId, Integer shippingId);
    //取消订单
    public ServerResponse cencalOrder(Integer userId,String orderNo);
    //得到加入订单购物车的信息
    public ServerResponse getOrderCartProduct(Integer userId);
    //订单详情
    public ServerResponse getOrderDetail(Integer userId,String orderNo);
    //得到订单列表
    public  ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);
    //后台订单列表
    public ServerResponse<PageInfo> manageList(int pageNum,int pageSize);
    //后台获取详情
    public ServerResponse<OrderVo> manageDetail(String orderNo);
    //管理订单查询
    public ServerResponse<PageInfo> manageSearch(String orderNo,int pageNum,int pageSize);
    //前台订单查询
    public ServerResponse<PageInfo> Search(Integer userId,String orderNo,int pageNum,int pageSize);
    //发货功能
    public ServerResponse<String> manageSendGoods(String orderNo,String ExpressOrder,String ExpressType);
}
