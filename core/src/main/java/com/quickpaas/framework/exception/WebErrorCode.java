package com.quickpaas.framework.exception;


public class WebErrorCode {

    public final static ErrorCode ERROR_404 = new ErrorCode(404, "未知的数据");
    public final static ErrorCode ERROR_500 = new ErrorCode(500, "系统错误");
    public final static ErrorCode CREATE_DTO_ERROR = new ErrorCode(1002, "创建DTO错误");
}
