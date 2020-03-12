package cn.iflags.Mall.util;

/**
 * 描述:跨平台资源配置路径
 * @author Vincent Vic
 * create 2020-02-18 14:43
 */


public class PathUtil {
    public static String getUploadResources(){
        if (OSinfo.isLinux()){
            return PropertiesUtil.getProperty("resources.linux.path");
        } else if (OSinfo.isWindows()){
            return PropertiesUtil.getProperty("resources.windows.path");
        }
        return null;
    }
}