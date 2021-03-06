package cn.iflags.Mall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * 描述:通用服务请求类
 * @author Vincent Vic
 * @create 2020-02-14 17:55
 */

//实现序列化接口
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)  //序列中评不能未空，为空去除
public class ServerResponse<T>  implements Serializable {

    private int status;
    private String msg;
    private T data;

    /**
     * 私有构造器
     */

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    /**
     * 判断是否成功
     * @return
     */
    @JsonIgnore  //不出现在json中
    public boolean isSeccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    /**
     * 公开构造器
     */

    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg, T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errormsg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errormsg);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int code,String errormsg){
        return new ServerResponse<T>(code,errormsg);
    }

}