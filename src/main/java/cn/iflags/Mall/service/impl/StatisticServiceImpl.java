package cn.iflags.Mall.service.impl;

import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.dao.OrderMapper;
import cn.iflags.Mall.dao.ProductMapper;
import cn.iflags.Mall.dao.UserMapper;
import cn.iflags.Mall.service.IStatisticService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 描述:系统信息服务类
 * @author Vincent Vic
 * create 2020-02-28 13:07
 */

@Service("iStatisticService")
public class StatisticServiceImpl implements IStatisticService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 用户数
     * 订单数
     * 商品数
     * 上架商品数
     * 已经付款了人数
     * 完成订单人数
     * @return
     */
    @Override
    public ServerResponse baseCount() {
        Map<String,Integer> resultMap = Maps.newHashMap();
        resultMap.put("UserConut",userMapper.userCount());
        resultMap.put("OrderConut",orderMapper.orderCount());
        resultMap.put("OrderPaidConut",orderMapper.orderPaidCount());
        resultMap.put("OrderCompleteConut",orderMapper.ordeEndCount());
        resultMap.put("ProductConut",productMapper.productCount());
        resultMap.put("ProductOnSaleConut",productMapper.productOnSaleCount());
        return ServerResponse.createBySuccess(resultMap);
    }
}