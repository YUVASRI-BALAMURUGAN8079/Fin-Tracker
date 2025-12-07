package com.tracker.error;


public class ErrorConstants {
    public static final ErrorMessages SUCCESS = new ErrorMessages(200, "Ok");
    public static final ErrorMessages DUPLICATE = new ErrorMessages(201, "Duplicate email id");
    public static final ErrorMessages INVALID_INPUT = new ErrorMessages(202, "Invalid Input");
    public static final ErrorMessages INVALID_CREDENTIALS = new ErrorMessages(202, "Invalid credentials");
    public static final ErrorMessages SESSION_EXPIRED = new ErrorMessages(203, "Duplicate email id");
    public static final ErrorMessages ACCESS_DENIED = new ErrorMessages(204, "Access denied");
}



