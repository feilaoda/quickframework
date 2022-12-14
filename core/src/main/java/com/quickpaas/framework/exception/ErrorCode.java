package com.quickpaas.framework.exception;

public class ErrorCode {
    private int code;
    private String message;

    public ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public static ErrorCode Code(int code, String message) {
        return new ErrorCode(code, message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
