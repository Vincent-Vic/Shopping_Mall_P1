package cn.iflags.Mall.controller.backend;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.ResponseCode;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.service.ICategoryService;
import cn.iflags.Mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 描述:分类管理模块
 * @author Vincent Vic
 * create 2020-02-16 9:56
 */


@Controller
@RequestMapping("/manage/category/")   //请求地址设置
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加分类
     * @param session 会话信息
     * @param categoryName 类名
     * @param parentId 父节点 @RequestParam(value = "parentId",defaultValue = "0") 设置默认值
     * @return
     */
    @RequestMapping(value = "add_categroy.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse addCategroy(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId){
        //获取当前用户
        User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentuser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }
        if ( !iUserService.checkAdminRole(currentuser).isSeccess()){//不是管理员
            return ServerResponse.createByErrorMessage("权限不足");
        }

        return iCategoryService.addCategory(categoryName,parentId);
    }

    //修改分类名称
    @RequestMapping(value = "updata_category_name.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse UpdataCategoryName(HttpSession session,Integer categoryId,String categoryName){
        //获取当前用户
        User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentuser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }
        if ( !iUserService.checkAdminRole(currentuser).isSeccess()){//不是管理员
            return ServerResponse.createByErrorMessage("权限不足");
        }

        return iCategoryService.UpdataCategoryName(categoryId,categoryName);
    }

    //得到分类
    @RequestMapping(value = "get_categoty.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse getChildrenParallelCategoty(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        //获取当前用户
        User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentuser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }
        if ( !iUserService.checkAdminRole(currentuser).isSeccess()){//不是管理员
            return ServerResponse.createByErrorMessage("权限不足");
        }

        return iCategoryService.getChildrenParallelCategoty(categoryId);
    }

    //查询当前节点的ID和递归子节点的ID
    @RequestMapping(value = "get_deep_categoty.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse getCategoryAndDeepChildCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        //获取当前用户
        User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentuser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }
        if ( !iUserService.checkAdminRole(currentuser).isSeccess()){//不是管理员
            return ServerResponse.createByErrorMessage("权限不足");
        }
        return iCategoryService.getChildrenParallelCategoty(categoryId);
    }


}

