package com.tracker.error;

import java.util.HashMap;
import java.util.Map;

public class FinTrackerException extends RuntimeException {
    private final ApiErrorInfo info;
    private Map<String, Object> data;

    public FinTrackerException(ApiErrorInfo info) {
        super(info.getMessage());
        this.info = info;
    }

    public FinTrackerException(ApiErrorInfo info, Throwable cause) {
        super(info.getMessage(), cause);
        this.info = info;
    }

    public FinTrackerException(ApiErrorInfo info, Map<String, Object> data) {
        super(info.getMessage());
        this.info = info;
        this.data = data;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(AppConstants.ERROR_CODE, info.getErrorCode());
        map.put(AppConstants.ERROR_MESSAGE, info.getMessage());
        map.put(AppConstants.DATA, data);
        return map;
    }

    public ApiErrorInfo getInfo() {
        return info;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
