package com.quickpaas.framework.exception;

public class QuickException extends RuntimeException{
    private int code;

    public QuickException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        code = errorCode.getCode();
    }

    public QuickException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        code = errorCode.getCode();
    }

    public QuickException(String message) {
        super(message);
    }

    public QuickException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuickException(Throwable cause) {
        super(cause);
    }

    public QuickException(String format, Object... data) {
        super(String.format(format.replaceAll("\\{}", "%s"), data));
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
