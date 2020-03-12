package cn.iflags.Mall.service;

import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.Shipping;
import cn.iflags.Mall.vo.CartVo;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * 描述:收货地址服务接口
 * @author Vincent Vic
 * create 2020-02-21 13:20
 */


public interface IShippingService {

    //增
    public ServerResponse add(Integer userId , Shipping shipping);
    //删
    public ServerResponse<String> deleteShipping(Integer userId,Integer shippingId);
    //改
    public ServerResponse update(Integer userId , Shipping shipping);
    //查
    public ServerResponse select(Integer userId,Integer shippingId);
    //列表
    public ServerResponse<PageInfo> list(Integer userId, int pageNum,  int pageSize);

}