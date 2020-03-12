package cn.iflags.Mall.service.impl;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.GenerateOrderNo;
import cn.iflags.Mall.common.ResponseCode;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.dao.*;
import cn.iflags.Mall.pojo.*;
import cn.iflags.Mall.service.IOrderService;
import cn.iflags.Mall.util.BigDecimalUtil;
import cn.iflags.Mall.util.PropertiesUtil;
import cn.iflags.Mall.vo.OrderItemVo;
import cn.iflags.Mall.vo.OrderProductVo;
import cn.iflags.Mall.vo.OrderVo;
import cn.iflags.Mall.vo.ShippingVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 描述:订单模块实现
 * @author Vincent Vic
 * create 2020-02-22 15:53
 */

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper ;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    // -----------------------前台代码块-------------------------

    //通过单个商品创建订单
    public ServerResponse createOrderByProduct(Integer userId, Integer productId,Integer quantity,Integer shippingId){
        //获取商品
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null  || product.getStatus() == Const.ProductStatusEnum.NOT_SALE.getCode())
            return ServerResponse.createByErrorMessage("商品不存在或已经下架");
        //校验库存
        if (quantity > product.getStock()){
            return  ServerResponse.createByErrorMessage("产品："+product.getName()+"库存不足");
        }
        //计算总价
        BigDecimal payment = BigDecimalUtil.mul(product.getPrice().doubleValue(),quantity.doubleValue());
        //生成订单
        Order order = this.assembleOrder(userId,shippingId,payment);
        if (order ==null){
            return ServerResponse.createByErrorMessage("订单创建失败!");
        }
        //加入订单商品
        OrderItem orderItem = new OrderItem(product);
        orderItem.setUserId(userId);
        orderItem.setOrderNo(order.getOrderNo());
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(payment);
        //orderItemMapper.insert(orderItem);
        List<OrderItem> orderItemList = Lists.newArrayList();
        orderItemList.add(orderItem);
        //mybatis 批量插入
        orderItemMapper.batchInsert(orderItemList);
        //减少库存增加销量
        this.reduceProductStockAndAddSales(orderItemList);
        //返回前端数据
        OrderVo orderVo = assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    //通过购物车创建订单
    public ServerResponse createOrderByCart(Integer userId, Integer shippingId){

        //从购物车里面获取数据
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);

        //得到购物车商品
        ServerResponse serverResponse = this.getCartOrderItem(userId,cartList);
        if (!serverResponse.isSeccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>)serverResponse.getData();
        if (CollectionUtils.isEmpty(orderItemList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //计算总价
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);
        Order order = this.assembleOrder(userId,shippingId,payment);
        if (order == null){
            return ServerResponse.createByErrorMessage("生成订单错误");
        }

        for (OrderItem orderItem : orderItemList){
            orderItem.setUserId(userId);
            orderItem.setOrderNo(order.getOrderNo());
        }
        //mybatis 批量插入
        orderItemMapper.batchInsert(orderItemList);
        //减少库存增加销量
        this.reduceProductStockAndAddSales(orderItemList);
        //清空购物车
       this.cleanCart(cartList);
       //返回前端数据
        OrderVo orderVo = assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    //取消订单
    public ServerResponse cencalOrder(Integer userId,String orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order == null)
            return ServerResponse.createByErrorMessage("用户订单不存在");
        if (order.getStatus() == Const.OrderStatusEnum.CANCELED.getCode())
            //商品已经取消，避免重复操作
            return ServerResponse.createByErrorMessage("商品已经取消，请不要重复操作！");

        if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()){
            //商品已经付款处理逻辑
            return ServerResponse.createByErrorMessage("商品已经支付，目前需要联系客服取消");
        }

        //取消订单
        Order cancelOrder = new Order();
        cancelOrder.setId(order.getId());
        cancelOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int row = orderMapper.updateByPrimaryKeySelective(cancelOrder);
        if (row == 0)
            return ServerResponse.createByErrorMessage("取消失败");

        //得到订单商品，恢复库存
        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId,orderNo);
        for (OrderItem orderItem : orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setId(orderItem.getProductId());
            product.setSales_volume(product.getSales_volume() - orderItem.getQuantity());
            product.setStock(product.getStock() + orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
        return ServerResponse.createBySuccessMessage("订单取消成功");

    }

    //订单商品
    public ServerResponse getOrderCartProduct(Integer userId){
        OrderProductVo orderProductVo = new OrderProductVo();
        //得到购物车商品
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
       //转变成订单商品
        ServerResponse serverResponse = this.getCartOrderItem(userId,cartList);
        if (!serverResponse.isSeccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>)serverResponse.getData();
        if (CollectionUtils.isEmpty(orderItemList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //计算总价
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);
        //
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList){
            orderItemVoList.add(new OrderItemVo(orderItem));
        }
        orderProductVo.setProductTotal(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVo);
    }

    public ServerResponse getOrderDetail(Integer userId,String orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order == null)
            return ServerResponse.createByErrorMessage("用户订单不存在");
        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId,orderNo);
        OrderVo orderVo = assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    //得到订单列表
    public  ServerResponse<PageInfo> getOrderList(Integer userId,int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        System.out.println(orderList.size());
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList,userId);
        PageInfo pageInfo = new PageInfo((orderList));
        pageInfo.setList(orderList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    //------------------------------管理后台---------------------------
    //管理系统列表
    public ServerResponse<PageInfo> manageList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectAllOrder();
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList,null);
        PageInfo pageInfo = new PageInfo((orderList));
        return ServerResponse.createBySuccess(pageInfo);
    }

    //管理系统列表
    public ServerResponse<OrderVo> manageDetail(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        OrderVo orderVo = assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    //管理订单查询
    public ServerResponse<PageInfo> manageSearch(String orderNo,int pageNum,int pageSize){

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        return getPageInfoServerResponse(orderNo, pageNum, pageSize, order);
    }

    //发货模块
    public ServerResponse<String> manageSendGoods(String orderNo,String ExpressOrder,String ExpressType){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null)
            return ServerResponse.createByErrorMessage("订单不存在");
        if (order.getStatus() == Const.OrderStatusEnum.NO_PAY.getCode())
            return ServerResponse.createByErrorMessage("订单未付款不能发货");
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        //判断发货清空
        if (ExpressOrder.equals("null") && ExpressType.equals("null")){ //都为空代表虚拟发货
            order.setDelivery_type(Const.Delivery.VIRTUAL.getCode());
        }  else if (ExpressOrder.equals("null") || ExpressType.equals("null")){//其中一个为空错误
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        } else {
            order.setDelivery_type(Const.Delivery.LOGISTICS.getCode());//物流发货
            updateOrder.setExpress_order(ExpressOrder); //加入物流信息
            updateOrder.setExpress_type(ExpressType);
        }
        updateOrder.setStatus(Const.OrderStatusEnum.SHIPPED.getCode()); //设置已经发货
        updateOrder.setSendTime(new Date());
        orderMapper.updateByPrimaryKeySelective(updateOrder);
        return ServerResponse.createBySuccessMessage("发货成功");
    }


    //前台搜索功能
    @Override
    public ServerResponse<PageInfo> Search(Integer userId,String orderNo, int pageNum, int pageSize) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        return getPageInfoServerResponse(orderNo, pageNum, pageSize, order);
    }

    //发货功能


    // ------------------------------私有方法--------------------------

    //前后台搜索通用组件
    private ServerResponse<PageInfo> getPageInfoServerResponse(String orderNo, int pageNum, int pageSize, Order order) {
        PageHelper.startPage(pageNum,pageSize);
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        OrderVo orderVo = assembleOrderVo(order,orderItemList);
        PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
        pageInfo.setList(Lists.newArrayList(orderVo));
        return ServerResponse.createBySuccess(pageInfo);
    }

    //组装订单列表信息
    private List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId){
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order :orderList){
            List<OrderItem> orderItemList = Lists.newArrayList();
            if (userId == null){
                //管理员不需要UserId
                orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
            }else {
                orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId, order.getOrderNo());
            }
            OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    //组合前端需要的订单信息
    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        //创建信息，并且大部分信息交给构造函数设置
        OrderVo orderVo = new OrderVo(order);
        //添加收货地址信息
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null){
            orderVo.setReceiverName(shipping.getReceiverName());
            ShippingVo shippingVo = new ShippingVo(shipping);
            orderVo.setShippingVo(shippingVo);
        }
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        //添加商品详情列表
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem :orderItemList){
            OrderItemVo orderItemVo = new OrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setItemVoList(orderItemVoList);
        return orderVo;
    }

    //清空购物车
    private void cleanCart(List<Cart> cartList){
        for (Cart cart:cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    //减少库存增加销售
    private void reduceProductStockAndAddSales(List<OrderItem> orderItemList){
        for (OrderItem orderItem : orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            //减少库存
            product.setStock(product.getStock() - orderItem.getQuantity());
            //增加销量
            product.setSales_volume(product.getSales_volume() + orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    //组合订单
    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment){
        Order order = new Order();
        String orderNo = GenerateOrderNo.generateOrderNo(userId.toString(),Const.SERVER_INDEX.PRODUCT_ORDER);
        int row=0;
        do {
            row = orderMapper.checkedOrderNo(orderNo);
        }while (row != 0);
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPaymentType(1);
        //发货时间&&付款时间在后续更新
        int rowCount = orderMapper.insert(order);
        if (rowCount == 0)
            return null;
        return order;
    }

    //计算总价
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal payment = new BigDecimal("0");
        for ( OrderItem orderItem : orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    //获取购物车里的商品加入订单的列表
    private ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){
        List<OrderItem> itemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空！");
        }
        for (Cart cartItem: cartList ) {
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if (product == null ){//商品不存在，告诉用户失效即可
                return  ServerResponse.createByErrorMessage("产品失效，无法购买");
            }
            //校验是否在售
            if ( product.getStatus() == Const.ProductStatusEnum.NOT_SALE.getCode()){
                return  ServerResponse.createByErrorMessage("产品："+product.getName()+"下架，无法购买");
            }
            System.out.println(cartItem.getQuantity()+","+product.getStock());
            //校验库存
            if (cartItem.getQuantity() > product.getStock()){
                return  ServerResponse.createByErrorMessage("产品："+product.getName()+"库存不足");
            }
            //加入订单商品
            OrderItem orderItem = new OrderItem(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity().doubleValue()));
            itemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(itemList);
    }


}