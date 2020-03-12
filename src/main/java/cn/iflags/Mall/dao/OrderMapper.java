package cn.iflags.Mall.dao;

import cn.iflags.Mall.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    //根据订单号查询
    Order selectByOrderNo(String orderNo);

    //统计订单总数
    int orderCount();

    //统计订单付款数
    int orderPaidCount();

    //订单完成数
    int ordeEndCount();

    //根据订单号查询
    int checkedOrderNo(String orderNo);

    //根据用户和订单查询订单
    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") String orderNo);

    //根据用户查询订单
    List<Order> selectByUserId(Integer userId);

    //查询订单
    List<Order> selectAllOrder();

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);
}