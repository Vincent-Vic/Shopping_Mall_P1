package cn.iflags.Mall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 描述:时间转换工具类
 * @author Vincent Vic
 * create 2020-02-17 17:22
 */


public class DateTimeUtil {
    //joda-time

    public static final String STANDARD_FORMAT ="yyyy-MM-dd HH:mm:ss";

    //str->Date
    public static Date strToDate(String dateTimeStr,String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }
    //Date->str
    public static String dateToStr(Date date,String formatStr){
        if (date == null) return StringUtils.EMPTY;
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    //str->Date
    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }
    //Date->str
    public static String dateToStr(Date date){
        if (date == null) return StringUtils.EMPTY;
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }

    public static String getDayByToday(){
        SimpleDateFormat formatter= new SimpleDateFormat("dd");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public static String getMonthByToday(){
        SimpleDateFormat formatter= new SimpleDateFormat("MM");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public static String getYearByToday(){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}