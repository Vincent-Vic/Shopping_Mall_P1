package cn.iflags.Mall.vo;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.pojo.Order;
import cn.iflags.Mall.util.DateTimeUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 描述:返回给前端的订单信息
 * @author Vincent Vic
 * create 2020-02-22 23:08
 */


public class OrderVo {

    private String orderNo;

    private BigDecimal payment;

    private Integer paymentType;

    private String paymentTypeDesc;

    private Integer postage;

    private String express_order;

    private String express_type;

    private Integer delivery_type;

    private String delivery_type_desc;

    private Integer status;

    private String statusDesc;

    private String paymentTime;

    private String sendTime;

    private String endTime;

    private String closeTime;

    private String createTime;

    private List<OrderItemVo> itemVoList;

    private String imageHost;

    private Integer shippingId;

    private String receiverName;

    private ShippingVo shippingVo;

    public OrderVo() {
    }

    public OrderVo(Order order) {
        this.orderNo = order.getOrderNo();
        this.payment = order.getPayment();
        this.paymentType = order.getPaymentType();
        this.paymentTypeDesc = Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValus();
        this.postage = order.getPostage();
        this.express_order = order.getExpress_order();
        this.express_type = order.getExpress_type();
        this.delivery_type = order.getDelivery_type();
        this.delivery_type_desc = Const.Delivery.codeOf(0).getValus();
        this.status = order.getStatus();
        this.statusDesc = Const.OrderStatusEnum.codeOf(order.getStatus()).getValue();
        this.shippingId = order.getShippingId();
        this.paymentTime = DateTimeUtil.dateToStr(order.getPaymentTime());
        this.sendTime = DateTimeUtil.dateToStr(order.getSendTime());
        this.endTime = DateTimeUtil.dateToStr(order.getEndTime());
        this.closeTime = DateTimeUtil.dateToStr(order.getCloseTime());
        this.createTime = DateTimeUtil.dateToStr(order.getCreateTime());
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentTypeDesc() {
        return paymentTypeDesc;
    }

    public void setPaymentTypeDesc(String paymentTypeDesc) {
        this.paymentTypeDesc = paymentTypeDesc;
    }

    public Integer getPostage() {
        return postage;
    }

    public void setPostage(Integer postage) {
        this.postage = postage;
    }

    public String getExpress_order() {
        return express_order;
    }

    public void setExpress_order(String express_order) {
        this.express_order = express_order;
    }

    public String getExpress_type() {
        return express_type;
    }

    public void setExpress_type(String express_type) {
        this.express_type = express_type;
    }

    public Integer getDelivery_type() {
        return delivery_type;
    }

    public void setDelivery_type(Integer delivery_type) {
        this.delivery_type = delivery_type;
    }

    public String getDelivery_type_desc() {
        return delivery_type_desc;
    }

    public void setDelivery_type_desc(String delivery_type_desc) {
        this.delivery_type_desc = delivery_type_desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<OrderItemVo> getItemVoList() {
        return itemVoList;
    }

    public void setItemVoList(List<OrderItemVo> itemVoList) {
        this.itemVoList = itemVoList;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public Integer getShippingId() {
        return shippingId;
    }

    public void setShippingId(Integer shippingId) {
        this.shippingId = shippingId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public ShippingVo getShippingVo() {
        return shippingVo;
    }

    public void setShippingVo(ShippingVo shippingVo) {
        this.shippingVo = shippingVo;
    }
}