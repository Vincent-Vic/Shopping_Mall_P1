package cn.iflags.Mall.dao;

import cn.iflags.Mall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    List<OrderItem> selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") String orderNo);

    List<OrderItem> selectByOrderNo( @Param("orderNo") String orderNo);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);
    //批量插入
    void batchInsert(@Param("orderItemList")List<OrderItem> orderItemList);

}