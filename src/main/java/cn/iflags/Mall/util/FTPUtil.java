package cn.iflags.Mall.util;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.service.impl.FileServiceImpl;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.List;

/**
 * 描述:上传文件工具
 * @author Vincent Vic
 * create 2020-02-17 20:49
 */


public class FTPUtil {

    private static Logger logger= LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp =PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public FTPUtil(String ip, int port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public static  ServerResponse upladFile(List<File> fileList,String suffix){

        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器");
        ServerResponse result = ftpUtil.uploadFile(suffix,fileList);
        logger.info("结束上传，上传结果:{}",result.getMsg());
        return result;
    }

    private ServerResponse uploadFile(String remotePath, List<File> fileList) {
        FileInputStream fis = null;

        //连接ftp服务器
        if (connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //设置成被动模式
                ftpClient.enterLocalPassiveMode();
                ftpClient.configure(new FTPClientConfig("com.zznode.tnms.ra.c11n.nj.resource.ftp.UnixFTPEntryParser"));
                logger.info("FTP登录状态：{}",ftpClient.getStatus());
                for (File fileItem: fileList ) {
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
                return ServerResponse.createBySuccessMessage("成功");
            } catch (IOException e) {
                logger.error("更改FTP目录异常",e);
                return ServerResponse.createBySuccessMessage("服务器异常Fx00D001");
            } finally {
                try {
                    fis.close();
                    ftpClient.disconnect();
                } catch (IOException e) {
                    logger.error("FTP关闭流失败",e);
                }
            }

        }
        return ServerResponse.createBySuccessMessage("服务组件连接失败 FX00CP02");
    }

    private boolean connectServer(String ip,int port,String user,String pwd){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip,port);
            isSuccess=ftpClient.login(user,pwd);
            logger.info("FTP登录状态：{}",ftpClient.getStatus());
            //判断登陆是否成功
           /* int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                logger.info("连接失败：{}",ftpClient.getStatus());
                ftpClient.disconnect();
                return false;
            }*/

        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
            isSuccess=false;
        }
        logger.info("FT连接成功");
        return isSuccess;
    }

    public static String getFtpIp() {
        return ftpIp;
    }

    public static void setFtpIp(String ftpIp) {
        FTPUtil.ftpIp = ftpIp;
    }

    public static String getFtpUser() {
        return ftpUser;
    }

    public static void setFtpUser(String ftpUser) {
        FTPUtil.ftpUser = ftpUser;
    }

    public static String getFtpPass() {
        return ftpPass;
    }

    public static void setFtpPass(String ftpPass) {
        FTPUtil.ftpPass = ftpPass;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}