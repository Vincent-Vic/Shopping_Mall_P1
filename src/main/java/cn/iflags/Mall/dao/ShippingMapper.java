package cn.iflags.Mall.dao;

import cn.iflags.Mall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByIdAndUserId(@Param("id") Integer id,@Param("userId") Integer userId);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    Shipping selectByShippingAndUserId(@Param("id") Integer id,@Param("userId") Integer userId);

    List<Shipping> selectByUserId(Integer userId);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int updateByIdAndShipping(Shipping record);
}