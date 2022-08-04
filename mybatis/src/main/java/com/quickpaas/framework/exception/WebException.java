package com.quickpaas.framework.exception;


public class WebException extends QuickException {
    public WebException(ErrorCode errorCode) {
        super(errorCode);
    }

    public WebException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
    public WebException(Throwable cause) {
        super(WebErrorCode.ERROR_500, cause);

    }
}
