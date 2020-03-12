package cn.iflags.Mall.service.impl;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.service.IFileService;
import cn.iflags.Mall.util.FTPUtil;
import cn.iflags.Mall.util.FileUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 描述:上传文件服务
 * @author Vincent Vic
 * create 2020-02-17 20:31
 */

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);

    //上传文件
    @Override
    public ServerResponse upload(MultipartFile file, String path, String suffix){
        String fileName = file.getOriginalFilename();

        //扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        logger.info("接到文件上传请求："+fileName+","+fileExtensionName);
        //校验是否为图片文件
        if (!FileUtil.isImageFileBySuffix(fileExtensionName))
            return ServerResponse.createByErrorMessage("非法上传");
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，上传文件的文件名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);
        //判断文件夹存不存在
        File fileDir = new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        //创建文件
        File targetFile = new File(path,uploadFileName);

        try {
            //上传文件
            file.transferTo(targetFile);
            /**
             * 直接指定目录
             */
            //将targetFile上传到FTP服务器
            ServerResponse status = FTPUtil.upladFile(Lists.newArrayList(targetFile),suffix);
            //不管是否完成，删除upload下面的文件
            targetFile.delete();
            //如果不成功返回状态码!
            if (!status.isSeccess()){
                return status;
            }
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return ServerResponse.createByErrorMessage("Const.FTPStatusEnum.EXCEPTION.getCode()");
        }
        return ServerResponse.createBySuccessMessage(targetFile.getName());

    }
}