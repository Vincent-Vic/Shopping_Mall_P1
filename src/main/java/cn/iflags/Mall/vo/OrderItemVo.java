package cn.iflags.Mall.vo;

import cn.iflags.Mall.pojo.OrderItem;
import cn.iflags.Mall.util.DateTimeUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述:订单详情
 * @author Vincent Vic
 * create 2020-02-22 23:14
 */


public class OrderItemVo {
    private String orderNo;

    private Integer productId;

    private String productName;

    private String productImage;

    private BigDecimal currentUnitPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    private String createTime;

    public OrderItemVo() {
    }

    public OrderItemVo(OrderItem orderItem) {
        this.orderNo = orderItem.getOrderNo();
        this.productId = orderItem.getProductId();
        this.productName = orderItem.getProductName();
        this.productImage = orderItem.getProductImage();
        this.currentUnitPrice = orderItem.getCurrentUnitPrice();
        this.quantity = orderItem.getQuantity();
        this.totalPrice = orderItem.getTotalPrice();
        this.createTime = DateTimeUtil.dateToStr(orderItem.getCreateTime());
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public BigDecimal getCurrentUnitPrice() {
        return currentUnitPrice;
    }

    public void setCurrentUnitPrice(BigDecimal currentUnitPrice) {
        this.currentUnitPrice = currentUnitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}