package com.kohia.galaxy.error;


public class ErrorConstants {
    public static final ApiErrorInfo SUCCESS = new ApiErrorInfo(200, "Ok");
    public static final ApiErrorInfo DUPLICATE = new ApiErrorInfo(201, "Duplicate email id");
    public static final ApiErrorInfo INVALID_INPUT = new ApiErrorInfo(202, "Invalid Input");
    public static final ApiErrorInfo INVALID_CREDENTIALS = new ApiErrorInfo(202, "Invalid credentials");
    public static final ApiErrorInfo SESSION_EXPIRED = new ApiErrorInfo(203, "Duplicate email id");
    public static final ApiErrorInfo ACCESS_DENIED = new ApiErrorInfo(204, "Access denied");
}



