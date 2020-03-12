package cn.iflags.Mall.service;

import cn.iflags.Mall.common.ServerResponse;
import org.springframework.stereotype.Service;

/**
 * 描述:系统基本信息
 * @author Vincent Vic
 * create 2020-02-28 13:05
 */


public interface IStatisticService {

    //基本统计
    ServerResponse baseCount();
}