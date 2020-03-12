package cn.iflags.Mall.service.impl;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.GenerateOrderNo;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.common.TokenChache;
import cn.iflags.Mall.dao.OrderMapper;
import cn.iflags.Mall.dao.UserMapper;
import cn.iflags.Mall.dao.UserWalletMapper;
import cn.iflags.Mall.pojo.Order;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.pojo.UserWallet;
import cn.iflags.Mall.service.IPayService;
import cn.iflags.Mall.service.IWalletService;
import cn.iflags.Mall.util.BigDecimalUtil;
import cn.iflags.Mall.util.MD5Util;
import cn.iflags.Mall.util.PathUtil;
import cn.iflags.Mall.util.PropertiesUtil;
import cn.iflags.Mall.vo.UserVo;
import com.alipay.demo.trade.model.GoodsDetail;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 *
 * @author Vincent Vic
 * create 2020-02-27 18:37
 */

@Service("iWalletService")
public class WalletServiceImpl implements IWalletService {

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private IPayService iPayService;

    @Override
    public ServerResponse Initialization(Integer userId) {
        UserWallet userWallet = new UserWallet();
        userWallet.setPayPassword(MD5Util.MD5EncodeUtf8("123456"));
        userWallet.setUserId(userId);
        userWalletMapper.insert(userWallet);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse getInformation(Integer userId) {
        UserWallet userWallet = userWalletMapper.selectByUserId(userId);
        userWallet.setPayPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(userWallet);
    }

    //忘记支付密码-重置
    @Override
    public ServerResponse<String> forgetRestPasword(Integer userId, String passwordNew, String forgetToken) {
        if (org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误：token需要传递");
        }
        User user =userMapper.selectByPrimaryKey(userId);
        String token = TokenChache.getKey(TokenChache.getTokenFprefix()+user.getUsername());
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if(StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int reslutCount = userWalletMapper.updataPasswordByUserId(userId,md5Password);
            if (reslutCount > 0){
                return ServerResponse.createBySuccessMessage("修改成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token错误，错误请重新重置密码");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public ServerResponse<String> restPasword(Integer userId, String passwordOld, String passwordNew) {
        //校验用户钱包旧支付密码，确定是这个用户
        int resulCount = userWalletMapper.cheackPassword(userId,MD5Util.MD5EncodeUtf8(passwordOld));
        if (resulCount == 0){
            return ServerResponse.createByErrorMessage("旧密码不正确");
        }
        UserWallet userWallet =userWalletMapper.selectByUserId(userId);
        userWallet.setPayPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updataCont = userWalletMapper.updateByPrimaryKeySelective(userWallet);
        if (updataCont == 0 ){
            return ServerResponse.createByErrorMessage("修改失败");
        }
        return ServerResponse.createByErrorMessage("修改成功");
    }

    public ServerResponse recharge(Integer userId,BigDecimal price, String suffix){
        //返回数据使用的map：orderNo && url
        Map<String,String> resultMap = Maps.newHashMap();
        //创建订单
        Order order = new Order();
        order.setPayment(price);
        order.setDelivery_type(0);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setShippingId(0);
        order.setPostage(0);
        order.setStatus(0);
        order.setOrderNo(GenerateOrderNo.generateOrderNo(userId.toString(),Const.SERVER_INDEX.RECHARGE));
        order.setUserId(userId);
        resultMap.put("orderNo",order.getOrderNo());
        int row = orderMapper.insert(order);
        if (row ==0)
            return ServerResponse.createByErrorMessage("订单创建失败");

        String path = PathUtil.getUploadResources() + suffix+"/";
        //组合支付宝需要的商品详情列表
        List<GoodsDetail> goodsDetailList = Lists.newArrayList();
        GoodsDetail goods = GoodsDetail.newInstance("0", "码农flag充值："+price.toString(), price.longValue(), 1);
        goodsDetailList.add(goods);
        //当面付功能
        ServerResponse response = iPayService.aliPay_precreate(order.getOrderNo(),order.getPayment(),goodsDetailList,path);
        if (!response.isSeccess()){
            resultMap.put("error",response.getMsg());
        }else {
            resultMap.put("qrUrl", PropertiesUtil.getProperty("ftp.server.http.prefix") + suffix + "/" + response.getMsg());
        }
        return ServerResponse.createBySuccess(resultMap);
    }
}