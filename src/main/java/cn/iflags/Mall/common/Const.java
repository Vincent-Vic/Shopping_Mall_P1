package cn.iflags.Mall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * 描述:
 *
 * @author Vincent Vic
 * @create 2020-02-14 19:22
 */


public class Const {
    //当前用户
    public static final String CURRENT_USER = "currentUser";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    //排序
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

    //角色：管理员，普通用户
    public interface Role{
        int ROLE_CUSTOMER = 0;  //普通用户
        int ROLE_ADMIN = 1; //管理员
    }

    //购物车状态
    public interface Cart{
        int CHECKED = 1; //选中状态
        int UN_CHECKED = 0; //未选中状态

        String LIMIT_NUM_FAIL ="LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS ="LIMIT_NUM_SUCCESS";
    }

    //服务器前缀
    public  interface SERVER_INDEX{
        String PRODUCT_ORDER = "14";
        String RECHARGE = "12";
    }

    //订单状态
    public enum OrderStatusEnum{

        NO_PAY(0,"未支付"),
        CANCELED(10,"已取消"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");


        OrderStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code){
            for(OrderStatusEnum orderStatusEnum : values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("么有找到对应的枚举");
        }
    }

    public interface  AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }


    //发货类型
    public enum Delivery {

        NOT_Delivery(0,"未发货"),
        LOGISTICS(1,"物流发货"),
        VIRTUAL(3,"虚拟发货");
        private String valus;
        private int code;

        Delivery(int code ,String valus) {
            this.valus = valus;
            this.code = code;
        }

        public static Delivery  codeOf(int code){
            for (Delivery delivery : values()){
                if (delivery.getCode() == code){
                    return delivery;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }


        public String getValus() {
            return valus;
        }

        public int getCode() {
            return code;
        }
    }

    //支付平台
    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝"),
        WECHATPAY(2,"微信"),
        WELLET(3,"钱包");
        PayPlatformEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    //支付类型
    public enum PaymentTypeEnum{
        ONLINE_PAY(1,"在线支付");
        private String valus;
        private int code;

        PaymentTypeEnum(int code ,String valus) {
            this.valus = valus;
            this.code = code;
        }

        public static PaymentTypeEnum codeOf(int code){
            for (PaymentTypeEnum paymentTypeEnum : values()){
                if (paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }


        public String getValus() {
            return valus;
        }

        public int getCode() {
            return code;
        }
    }

    //商品上下架状态
    public enum ProductStatusEnum{
        NOT_SALE(0,"下架"),
        ON_SALE(1,"在售");
        private String valus;
        private int code;

        ProductStatusEnum(int code ,String valus) {
            this.valus = valus;
            this.code = code;
        }

        public String getValus() {
            return valus;
        }

        public int getCode() {
            return code;
        }
    }

}