package cn.iflags.Mall.controller.backend;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.Conversation;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.service.IStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 描述:获取系统信息
 * @author Vincent Vic
 * create 2020-02-28 12:59
 */

@Controller
@RequestMapping("/manage/statistic/")   //请求地址设置
public class StatisticController {


    @Autowired
    private IStatisticService iStatisticService;

    /**
     * 用户数
     * 订单数
     * 商品数
     * 上架商品数
     * 已经付款了人数
     * 完成订单人数
     * @return
     */
    @RequestMapping(value = "base_count.do",method = RequestMethod.POST)
    @ResponseBody   //自动序列化Json
    public ServerResponse baseCount(HttpSession session){
        ServerResponse response = Conversation.checkAdminRole(session);
        if (!response.isSeccess()){
            return response;
        }
        return iStatisticService.baseCount();
    }

}