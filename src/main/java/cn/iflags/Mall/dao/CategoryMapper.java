package cn.iflags.Mall.dao;

import cn.iflags.Mall.pojo.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    //通过ID修改分类名称
    int UpdataCategoryNameById(@Param("categoryName")String categoryName, @Param("categoryId")Integer categoryId);

    //获取子节点分类list
    List<Category> selectCategoryCildrenByParentId(Integer ParentId);

    //校验分类名称是否已存在
    int cheackCategoryName(String categoryName);

}