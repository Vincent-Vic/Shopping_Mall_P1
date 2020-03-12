package cn.iflags.Mall.common;

/**
 * 描述: 请求状态
 * @author Vincent Vic
 * @create 2020-02-14 18:10
 */


public enum  ResponseCode {
    SUCCESS(0,"SUCCESS"),//成功状态
    ERROR(1,"ERROR"),//失败状态
    NEED_LOGIN(10,"NEED_LOGIN"),//需要登录
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT"),//非法参数
    NO_AUTHORITY(30,"NO_AUTHORITY"); // 无权限
    private final int code;
    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}