package cn.iflags.Mall.common;

/**
 * 描述:生成打订单号
 * @author Vincent Vic
 * create 2020-02-22 20:48
 */

import cn.iflags.Mall.util.DateTimeUtil;
import java.util.Random;
import java.util.Date;
/**
 *         1-2:服务器增值
 *         3-4:随机
 *         5-6:日
 *         7-8:服务器标识 初始值10 十六进制（不用字母）
 *         9-10:随机
 *         11-12:用户id后两位
 *         13-14:随机
 */

public class GenerateOrderNo {

    //服务器标识后期采用数据库获取
    private static String SERVER_HEX_ID = "11";


    public static String generateOrderNo(String userId,String SERVER_INDEX){
        String userid = userId;
        if (userId.length() < 2){
            userId = "0" + userId;
        }
        String orderNo = new StringBuffer().append(SERVER_INDEX)//1-2:服务器递增值
                .append(getRandmon())                           //3-4:随机
                .append(DateTimeUtil.getDayByToday())           //5-6:日
                .append(SERVER_HEX_ID)        //7-8:服务器标识 初始值10 十六进制（不用字母）
                .append(getRandmon())         //9-10:随机
                .append(userId.substring(userId.length()-2))      //11-12:用户id后两位
                .append(getRandmon()).toString();                   //13-14:随机
        System.out.println(DateTimeUtil.getDayByToday());
        return orderNo;
    }

    public static String SerialNumber(String OrderNo){
        Date date = new Date();// 获取当前时间
        return DateTimeUtil.dateToStr(date,"yyyyMMddhhmmss")+OrderNo;
    }
    private static String getRandmon(){
        Random random = new Random(System.currentTimeMillis());
        return new StringBuffer().append(random.nextInt(89)+10).toString();
    }
}