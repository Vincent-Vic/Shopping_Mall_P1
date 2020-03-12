package cn.iflags.Mall.service.impl;

import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.dao.CategoryMapper;
import cn.iflags.Mall.pojo.Category;
import cn.iflags.Mall.service.ICategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 描述:
 *
 * @author Vincent Vic
 * create 2020-02-16 10:21
 */

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    //日志
    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    CategoryMapper categoryMapper;

    //添加分类
    @Override
    public ServerResponse addCategory(String categoryName,Integer parentId){
        if (parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加分类参数错误");
        }
        int rowCount = categoryMapper.cheackCategoryName(categoryName);
        if (rowCount > 0) return ServerResponse.createByErrorMessage("分类已存在");
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true); //分类可用

        int resultCount = categoryMapper.insert(category);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("添加分类失败");
        }
        return ServerResponse.createBySuccessMessage("添加分类成功");

    }

    //修改分类名称
    @Override
    public ServerResponse UpdataCategoryName(Integer categoryId, String categoryName) {
        if (categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("修改分类名参数错误");
        }
        int resultCount = categoryMapper.UpdataCategoryNameById(categoryName,categoryId);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("修改分类名失败");
        }
        return ServerResponse.createBySuccessMessage("修改分类名成功");
    }

    //获取字节点分类
    @Override
    public ServerResponse getChildrenParallelCategoty(Integer categoryId) {
        if (categoryId == null){
            return ServerResponse.createByErrorMessage("获取分类参数错误");
        }
        List<Category> categoryList = categoryMapper.selectCategoryCildrenByParentId(categoryId);
        if (categoryList == null){
            logger.info("父节点下无分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    //递归查询本节点id及孩子节点id
    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> categoryList = Lists.newArrayList();
        if (categoryId != null){
            for (Category category : categorySet){
                categoryList.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    //递归查询分类
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if ( category != null){
            categorySet.add(category);
        }
        //递归退出条件
        List<Category> categoryList = categoryMapper.selectCategoryCildrenByParentId(categoryId);
        for (Category categoryItem : categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
}