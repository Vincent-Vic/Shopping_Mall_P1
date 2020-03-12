package cn.iflags.Mall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 描述:购物车信息
 * @author Vincent Vic
 * create 2020-02-23 13:53
 */


public class OrderProductVo {
    private List<OrderItemVo> orderItemVoList;
    private BigDecimal productTotal;
    private String ImageHost;

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public BigDecimal getProductTotal() {
        return productTotal;
    }

    public void setProductTotal(BigDecimal productTotal) {
        this.productTotal = productTotal;
    }

    public String getImageHost() {
        return ImageHost;
    }

    public void setImageHost(String imageHost) {
        ImageHost = imageHost;
    }
}