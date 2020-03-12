package cn.iflags.Mall.service;

import cn.iflags.Mall.common.ServerResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 描述:
 *
 * @author Vincent Vic
 * create 2020-02-16 10:20
 */


public interface ICategoryService {

    //添加分类
    public ServerResponse addCategory(String categoryName, Integer parentId);
    //修改分类名称
    public ServerResponse UpdataCategoryName(Integer categoryId, String categoryName);
    //获取字节点分类
    public ServerResponse getChildrenParallelCategoty(Integer categoryId);
    //递归查询本节点id及孩子节点id
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);

    }