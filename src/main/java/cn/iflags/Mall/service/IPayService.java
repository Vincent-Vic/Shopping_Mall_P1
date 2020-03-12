package cn.iflags.Mall.service;

import cn.iflags.Mall.common.ServerResponse;
import com.alipay.demo.trade.model.GoodsDetail;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

/**
 * 描述:支付服务接口
 * @author Vincent Vic
 * create 2020-02-26 17:16
 */


public interface IPayService {

    //支付宝接口
    ServerResponse aliPay(String orderNo, Integer userId, String suffix);

    //支付宝回调实现
    Object alipay_callback(HttpServletRequest request);

    //查询订单状态
    ServerResponse<Boolean> queryOrderPayStatus(Integer userId, String orderNo);

    //当面付功能
    ServerResponse aliPay_precreate(String orderNo, BigDecimal totalParce, List<GoodsDetail> goodsDetailList, String path);

    //钱包支付
    ServerResponse walletPay(Integer userId, String orderNo, String password);
}
