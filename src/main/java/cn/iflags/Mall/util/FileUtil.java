package cn.iflags.Mall.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 描述:文件工具
 * @author Vincent Vic
 * create 2020-02-29 13:08
 */


public class FileUtil {

    /**
     * 判断是否是图片
     * @param Suffix 不包括小数点
     * @return
     */
    public static Boolean isImageFileBySuffix(String Suffix){
        Suffix = Suffix.toLowerCase();
        //图片格式
        String[] FILETYPES = new String[]{
                "jpg", "bmp", "jpeg", "png", "gif",
                "JPG", "BMP", "JPEG", "PNG", "GIF"
        };

        if(!StringUtils.isBlank(Suffix)){
            for (int i = 0; i < FILETYPES.length; i++) {
                String fileType = FILETYPES[i];
                if (Suffix.compareTo(fileType) == 0) {
                    return true;
                }
            }
        }
        return false;
    }


    public static Boolean isImageFile(String img){
            Boolean flag =false;
             img = img.toLowerCase();
            //图片格式
        String[] FILETYPES = new String[]{
                ".jpg", ".bmp", ".jpeg", ".png", ".gif",
                ".JPG", ".BMP", ".JPEG", ".PNG", ".GIF"
        };
            if(!StringUtils.isBlank(img)){
                for (int i = 0; i < FILETYPES.length; i++) {
                    String fileType = FILETYPES[i];
                    if (img.endsWith(fileType)) {
                        flag = true;
                        break;
                    }
                }
            }

            return flag;

    }
}