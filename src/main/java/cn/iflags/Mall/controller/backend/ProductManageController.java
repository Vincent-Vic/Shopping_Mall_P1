package cn.iflags.Mall.controller.backend;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.Conversation;
import cn.iflags.Mall.common.ResponseCode;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.Product;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.service.IFileService;
import cn.iflags.Mall.service.IProductService;
import cn.iflags.Mall.service.IUserService;
import cn.iflags.Mall.util.PathUtil;
import cn.iflags.Mall.util.PropertiesUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 描述:后台商品管理
 * @author Vincent Vic
 * create 2020-02-17 14:37
 */

@Controller
@RequestMapping("/manage/product/")   //请求地址设置
public class ProductManageController {

    @Autowired
    private IUserService iUserService;


    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    //商品保存
    @RequestMapping(value = "product_save.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse  productSave(HttpSession session, Product product){
        ServerResponse response = Conversation.checkAdminRole(session);
        if (!response.isSeccess()) return response;
        return iProductService.SaveOrUptateProduct(product);
    }

    //商品上下架
    @RequestMapping(value = "set_sale_status.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse  setSaleStatus(HttpSession session, Integer productId,Integer status){
        ServerResponse response = Conversation.checkAdminRole(session);
        if (!response.isSeccess()) return response;
        return iProductService.setSaleStatus(productId,status);
    }

    //获取产品详情
    @RequestMapping(value = "get_detail.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse  getDetail(HttpSession session, Integer productId){
        ServerResponse response = Conversation.checkAdminRole(session);
        if (!response.isSeccess()) return response;
        return iProductService.manageProductDetail(productId);
    }

    //获取产品列表
    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse  getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        ServerResponse response = Conversation.checkAdminRole(session);
        if (!response.isSeccess()) return response;
        return iProductService.getProductList(pageNum,pageSize);
    }

    //获取产品列表
    @RequestMapping(value = "search.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse  productSearch(HttpSession session, String productName,Integer productId,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        ServerResponse response = Conversation.checkAdminRole(session);
        if (!response.isSeccess()) return response;
        return iProductService.searchProduct(productName,productId,pageNum,pageSize);
    }



    //获取文件上传
    @RequestMapping(value = "upload.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        ServerResponse response = Conversation.checkAdminRole(session);
        if (!response.isSeccess()) return response;
        //采用ftp转存机制
        String path = request.getSession().getServletContext().getRealPath("upload");
        String suffix = "img";
        //String path = PathUtil.getUploadResources() + "img";
        if (path == null) return ServerResponse.createByErrorMessage("资源路径错误");
        ServerResponse targetFileName = iFileService.upload(file,path,suffix);
        //检查上传状态
        if (!targetFileName.isSeccess())
            return targetFileName;

        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+"img/"+targetFileName.getMsg();
        Map fileMap = Maps.newHashMap();
        fileMap.put("uri",targetFileName.getMsg());
        fileMap.put("url",url);
        return ServerResponse.createBySuccess(fileMap);
    }

    //获取富文本文件上传
    @RequestMapping(value = "richtext_img_upload.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        ServerResponse responses = Conversation.checkAdminRole(session);
        if (!responses.isSeccess()){
            resultMap.put("success",false);
            resultMap.put("msg","未登录/无权限");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }

        String path = request.getSession().getServletContext().getRealPath("upload");
        String suffix = "";
        ServerResponse targetFileName = iFileService.upload(file,path,suffix);
        //检查上传状态
        if (!targetFileName.isSeccess()){
            resultMap.put("success",false);
            resultMap.put("msg","上传失败");
        } else {
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
        }
        return resultMap;
    }
}