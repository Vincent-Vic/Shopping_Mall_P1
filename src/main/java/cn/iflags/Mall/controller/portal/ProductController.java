package cn.iflags.Mall.controller.portal;

import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.service.IProductService;
import cn.iflags.Mall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 描述:前台商品管理
 * @author Vincent Vic
 * create 2020-02-19 14:34
 */

@Controller
@RequestMapping("/product/")   //请求地址设置
public class ProductController {

    @Autowired
    private IProductService iProductService;

    //获取产品详情
    @RequestMapping(value = "get_detail.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<ProductDetailVo> getDetail(Integer productId){
        return iProductService.manageProductDetail(productId);
    }

    //获取产品列表
    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false )String keyword,
                                         @RequestParam(value = "categotyId",required = false )Integer categotyId,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
      //  return iProductService.manageProductDetail(0);
        return iProductService.getProductByKeywordCategory(keyword,categotyId,pageNum,pageSize,orderBy);
    }
}