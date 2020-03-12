package cn.iflags.Mall.service.impl;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.GenerateOrderNo;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.dao.OrderItemMapper;
import cn.iflags.Mall.dao.OrderMapper;
import cn.iflags.Mall.dao.PayInfoMapper;
import cn.iflags.Mall.dao.UserWalletMapper;
import cn.iflags.Mall.pojo.Order;
import cn.iflags.Mall.pojo.OrderItem;
import cn.iflags.Mall.pojo.PayInfo;
import cn.iflags.Mall.pojo.UserWallet;
import cn.iflags.Mall.service.IPayService;
import cn.iflags.Mall.util.*;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 描述:支付服务实现
 * 部分功能拷贝于支付宝dome 原作者 liuyangkly
 * @author Vincent Vic
 * create 2020-02-26 17:16
 */

@Service("iPayService")
public class PayServiceImpl implements IPayService {

    private static Logger log = LoggerFactory.getLogger(PayServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private UserWalletMapper userWalletMapper;

    public ServerResponse aliPay(String orderNo,Integer userId,String suffix){
       //返回数据使用的map：orderNo && url
        Map<String,String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        resultMap.put("orderNo",order.getOrderNo());
        String path = PathUtil.getUploadResources() + suffix+"/";
        //组合支付宝需要的商品详情列表
        List<GoodsDetail> goodsDetailList = getGoodsDetailList(userId,orderNo);
        //当面付功能
        ServerResponse response = aliPay_precreate(orderNo,order.getPayment(),goodsDetailList,path);
        if (!response.isSeccess()){
            resultMap.put("error",response.getMsg());
        }else {
            resultMap.put("qrUrl", PropertiesUtil.getProperty("ftp.server.http.prefix") + suffix + "/" + response.getMsg());
        }
        return ServerResponse.createBySuccess(resultMap);
    }

    //回调处理实现
    public Object alipay_callback(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        for(Iterator iter = requestParams.keySet().iterator();iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i = 0 ; i <values.length;i++){

                valueStr = (i == values.length -1)?valueStr + values[i]:valueStr + values[i]+",";
            }
            params.put(name,valueStr);
        }
        log.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        //非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.

        params.remove("sign_type");
        //验证回调的正确性，并且避免重复通知
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());

            if (!alipayRSACheckedV2){
                log.error("支付宝验证失败");
                return ServerResponse.createByErrorMessage("非法请求");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝回调异常",e);
        }
        ServerResponse response = CheckCommodity(params);
        if (response.isSeccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    //查询数据
    @Override
    public ServerResponse<Boolean> queryOrderPayStatus(Integer userId, String orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order == null)
            return ServerResponse.createByErrorMessage("用户订单不存在！！！");
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode())
            return ServerResponse.createBySuccess();
        return ServerResponse.createByError();
    }


    //回调检查数据，更新数据
    private ServerResponse CheckCommodity(Map<String,String> params){
        //取出订单
        String orderNo = params.get("out_trade_no");
        //判断订单是否为自己商城订单
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null)
            return ServerResponse.createByErrorMessage("非商城订单，回调忽略");
        //判断是否已经支付
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode())
            return ServerResponse.createBySuccess("支付宝重复调用");
        //判断订单金额是否一致
        String totalAmount = params.get("total_amount");
        if (!order.getPayment().toString().equals(totalAmount))
            return ServerResponse.createByErrorMessage("订单金额不一致！");
        //判断支付状态
        String tradeStatus = params.get("trade_status");
        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            log.info(orderNo+"  支付成功");
            //更新付款时间
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            //设置支付状态
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            //如果是充值订单，直接充值，并且完成订单
            if (orderNo.substring(0,2).equals(Const.SERVER_INDEX.RECHARGE)){
                log.info("充值订单");
                UserWallet userWallet = userWalletMapper.selectByUserId(order.getUserId());
                userWallet.setBalance(BigDecimalUtil.add(userWallet.getBalance().doubleValue(),new BigDecimal(totalAmount).doubleValue()));
                order.setStatus(Const.OrderStatusEnum.ORDER_SUCCESS.getCode());
                userWalletMapper.updateByPrimaryKeySelective(userWallet);
            }
            orderMapper.updateByPrimaryKeySelective(order);
        }
        //获取支付宝订单号
        String tradeNo = params.get("trade_no");
        PayInfo payInfo = new PayInfo(null,order.getUserId(),orderNo,
                Const.PayPlatformEnum.ALIPAY.getCode(),tradeNo,tradeStatus,null,null);
        //插入数据
        payInfoMapper.insert(payInfo);


        return ServerResponse.createBySuccess();
    }

    //支付宝当面付商品信息组合
    private List<GoodsDetail> getGoodsDetailList(Integer userId,String orderNo){
        // 支付宝商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        //获取订单商品
        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId,orderNo);
        for (OrderItem orderItem:orderItemList){
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(), orderItem.getQuantity());
            goodsDetailList.add(goods);

        }

        return goodsDetailList;
    }

    // 当面付2.0生成支付二维码
    //修改于支付宝dome作者： liuyangkly
    public ServerResponse aliPay_precreate(String orderNo, BigDecimal totalParce,List<GoodsDetail> goodsDetailList, String path) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = orderNo;

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuffer().append("码农flag[扫码支付]-订单号：").append(orderNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = totalParce.toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuffer().append("订单").append(orderNo).append(" 共付款 ").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "iflags";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "iflags.cn";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";


        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        //支付宝当面付2.0服务
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                //检查路径存不存在，不存在创建路径
                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String filePath = String.format(path + "/qr-%s.png",
                        response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                //生成二维码
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                log.info("filePath:" + filePath);
                return ServerResponse.createBySuccessMessage(qrFileName);
            //                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");

        }
    }

    @Override
    public ServerResponse walletPay(Integer userId,String orderNo,String password) {

        Order order = orderMapper.selectByOrderNo(orderNo);
        //校验密码
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        UserWallet userWallet = userWalletMapper.selectByUserIdPassword(userId,md5Password);
        if (userWallet == null) {
            return ServerResponse.createByErrorMessage("支付密码错误");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createByErrorMessage("已支付,不需要重复支付");
        }

        if (userWallet.getBalance().compareTo(order.getPayment()) == -1 ) {
            return ServerResponse.createByErrorMessage("余额不足");
        }
        userWallet.setBalance(BigDecimalUtil.sub(userWallet.getBalance().doubleValue(),order.getPayment().doubleValue()));
        int row = userWalletMapper.updateByPrimaryKeySelective(userWallet);
        if (row == 0 ){
            return ServerResponse.createByErrorMessage("交易失败");
        }
        order.setStatus(Const.OrderStatusEnum.PAID.getCode());
        row = orderMapper.updateByPrimaryKeySelective(order);
        if (row == 0 ){
            log.error("订单更新失败:"+orderNo);
            return ServerResponse.createByErrorMessage("订单更新失败");
        }
        //插入流水账
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(userId);
        payInfo.setOrderNo(orderNo);
        payInfo.setPayPlatform(Const.PayPlatformEnum.WELLET.getCode());
        payInfo.setPlatformStatus(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS);
        payInfo.setPlatformNumber(GenerateOrderNo.SerialNumber(orderNo));
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccessMessage("付款成功");
    }

    // 简单打印应答
    //  by liuyangkly
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
}