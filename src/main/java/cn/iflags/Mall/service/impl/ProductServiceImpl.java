package cn.iflags.Mall.service.impl;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.ResponseCode;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.dao.CategoryMapper;
import cn.iflags.Mall.dao.ProductMapper;
import cn.iflags.Mall.pojo.Category;
import cn.iflags.Mall.pojo.Product;
import cn.iflags.Mall.service.IProductService;
import cn.iflags.Mall.service.ICategoryService;
import cn.iflags.Mall.util.DateTimeUtil;
import cn.iflags.Mall.util.PropertiesUtil;
import cn.iflags.Mall.vo.ProductDetailVo;
import cn.iflags.Mall.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:商品管理实现类
 * @author Vincent Vic
 * create 2020-02-17 14:47
 */

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    //保存/更新产品
    @Override
    public ServerResponse SaveOrUptateProduct(Product product){
        if (product == null)
            return ServerResponse.createByErrorMessage("新增/更新商品错误");
        if (StringUtils.isNotBlank(product.getSubImages())){
            String[] subImageArray = product.getSubImages().split(",");
            if (subImageArray.length > 0){
                product.setMainImage(subImageArray[0]);
            }
        }
        if (product.getId() != null){
            int rowCount = productMapper.updateByPrimaryKeySelective(product);
            if (rowCount > 0)  return ServerResponse.createBySuccessMessage("更新产品成功");
            else return ServerResponse.createByErrorMessage("更新失败");
        } else {
            int rowCount = productMapper.insert(product);
            if (rowCount > 0)  return ServerResponse.createBySuccessMessage("添加产品成功");
            else return ServerResponse.createByErrorMessage("添加失败");
        }
    }

    //修改商品销售状态
    @Override
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if (productId == null || status == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0){
            return ServerResponse.createBySuccessMessage("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    //管理获取产品详情
    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if (productId == null )
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null)
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    //前台获取产品详情
    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if (productId == null )
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null)
            return ServerResponse.createByErrorMessage("产品已删除");
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode())
            return ServerResponse.createByErrorMessage("产品已下架");
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    //获取商品列表
    @Override
    public ServerResponse getProductList(int pageNum, int pageSize) {
        //startPage--start
        //填充自己的sql查询逻辑
        //pageHelper - 收尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = new ArrayList<>();
        for (Product productItem:productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);

        return ServerResponse.createBySuccess(pageResult);
    }

    //搜索商品功能
    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(productName)){
            productName = new StringBuffer().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);
        List<ProductListVo> productListVoList = new ArrayList<>();
        for (Product productItem:productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);

        return ServerResponse.createBySuccess(pageResult);

    }

    //根据关键词分类找到商品信息
    public ServerResponse getProductByKeywordCategory(String keyword,Integer categotyId, int pageNum, int pageSize,String orderBy){
        if (StringUtils.isBlank(keyword) && categotyId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<>();
        if (categotyId != null){
            Category category = categoryMapper.selectByPrimaryKey(categotyId);
            //没有分类的情况
            if (category == null && StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVos= Lists.newArrayList();
                return ServerResponse.createBySuccess(new PageInfo(productListVos));
            }
            //获取分类
            categoryIdList =iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if (StringUtils.isNotBlank(keyword))
            keyword =new StringBuffer().append("%").append(keyword).append("%").toString();
        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)){
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        //获取产品列表
        List<Product> productList=productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null:keyword,categoryIdList.size()==0 ? null:categoryIdList);
        //加入列表信息
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product:productList){
            System.out.println(product.getCategoryId()+","+product.getMainImage());
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo((productList));
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    //添加销量
    public ServerResponse addSalesVolume(Integer productId,Integer addNum){
        if (productId == null || addNum == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        int rowCount =productMapper.updateAddSalesVolume(productId,addNum);
        if (rowCount > 0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByErrorMessage("添加: "+productId+" 销售失败");
    }

    //添加销量
    public ServerResponse reduceSalesVolume(Integer productId,Integer reduceNum){
        if (productId == null || reduceNum == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        int rowCount =productMapper.updateReduceSalesVolume(productId,reduceNum);
        if (rowCount > 0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByErrorMessage("减少: "+productId+" 销售失败");
    }

    //组合商品详情
    private ProductDetailVo  assembleProductDetailVo(Product product){
        //采用构造函数赋值大部分值
        ProductDetailVo productDetailVo = new ProductDetailVo(product);
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.iflags.cn/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null)
            productDetailVo.setParentCategoryId(0); //默认根节点
        else
            productDetailVo.setParentCategoryId(category.getParentId()); //默认根节点
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    //组合产品列表值
    private ProductListVo assembleProductListVo(Product product){
        //采用构造函数赋值大部分值
        ProductListVo productListVo = new ProductListVo(product);
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.iflags.cn/"));
        return productListVo;
    }
}