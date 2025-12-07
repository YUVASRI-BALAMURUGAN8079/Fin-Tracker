package com.kohia.galaxy.error;

public class ApiErrorInfo {

    private final int errorCode;
    private final String message;

    public ApiErrorInfo(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

}
