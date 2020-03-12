package cn.iflags.Mall.dao;

import cn.iflags.Mall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    //增加销量
    int updateAddSalesVolume(@Param("productId") Integer productId,@Param("addNum")int addNum);

    //减少销量
    int updateReduceSalesVolume(@Param("productId") Integer productId,@Param("reduceNum")int reduceNum);

    List<Product> selectList();

    List<Product> selectByNameAndProductId(@Param("productName")String productName,@Param("productId") Integer productId);

    List<Product> selectByNameAndCategoryIds(@Param("productName")String productName,@Param("categoryIdList") List<Integer> categoryIdList);

    int productCount();

    int productOnSaleCount();
}