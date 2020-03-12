package cn.iflags.Mall.service;

import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.Product;
import cn.iflags.Mall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

/**
 * 描述:
 *
 * @author Vincent Vic
 * create 2020-02-17 14:46
 */

@Service("iProductService")
public interface IProductService {

    //保存或者更新商品
    public ServerResponse SaveOrUptateProduct(Product product);
    //修改商品销售状态
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status);
    //管理获取产品详情
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
    //前台商品详情
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
    //后台获取商品列表
    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize);
    //搜索商品
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);
    //前台产品搜索商品列表
    public ServerResponse getProductByKeywordCategory(String keyword,Integer categotyId, int pageNum, int pageSize,String orderBy);
}