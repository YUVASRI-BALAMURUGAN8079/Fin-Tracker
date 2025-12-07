package com.tracker.error;

public class ErrorMessages {

    private final int errorCode;
    private final String message;

    public ErrorMessages(int errorCode, String message) {
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
