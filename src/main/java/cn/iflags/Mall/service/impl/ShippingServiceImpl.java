package cn.iflags.Mall.service.impl;

import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.dao.ShippingMapper;
import cn.iflags.Mall.pojo.Shipping;
import cn.iflags.Mall.service.IShippingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 描述:收货地址模块服务
 * @author Vincent Vic
 * create 2020-02-21 13:20
 */

@Service("iShippingService")
public class ShippingServiceImpl  implements IShippingService {


    @Autowired
    private ShippingMapper shippingMapper;

    //添加
    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount > 0){
            Map result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建收货地址成功",result);
        }
        return ServerResponse.createByErrorMessage("新增收货地址失败");
    }

    //删除
    @Override
    public ServerResponse<String> deleteShipping(Integer userId,Integer shippingId) {
        int rowCount = shippingMapper.deleteByIdAndUserId(shippingId,userId);
        if (rowCount > 0){
            return ServerResponse.createBySuccess("删除收货地址成功");
        }
        return ServerResponse.createByErrorMessage("删除收货地址失败");
    }

    //更新
    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByIdAndShipping(shipping);
        if (rowCount > 0){
           return ServerResponse.createBySuccess("更新收货地址成功");
        }
        return ServerResponse.createByErrorMessage("更新收货地址失败");
    }

    //查询
    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
        Shipping result = shippingMapper.selectByShippingAndUserId(shippingId,userId);
        if (result != null){
            return ServerResponse.createBySuccess("查询收货地址成功",result);
        }
        return ServerResponse.createByErrorMessage("查询收货地址失败");
    }

    //获取列表
    @Override
    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        pageInfo.setList(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}