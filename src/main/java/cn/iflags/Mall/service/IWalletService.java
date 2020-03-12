package cn.iflags.Mall.service;

import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.vo.UserVo;

import java.math.BigDecimal;

/**
 * 描述:钱包服务服务接口
 * @author Vincent Vic
 * create 2020-02-27 18:35
 */


public interface IWalletService {
    //初始化钱包，给注册使用
    ServerResponse Initialization(Integer userId);
    //后期钱包信息
    ServerResponse getInformation(Integer userId);
    //修改支付密码-忘记密码
    ServerResponse<String> forgetRestPasword(Integer userId, String passwordNew, String forgetToken);
    //修改密码-通过密码
    ServerResponse<String> restPasword(Integer userId, String passwordOld, String passwordNew);
    //充值
    ServerResponse recharge(Integer userId, BigDecimal price, String suffix);

}
