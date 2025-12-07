package com.tracker.error;

import java.util.HashMap;
import java.util.Map;

public class FinTrackerException extends RuntimeException {
    private final ErrorMessages info;
    private Map<String, Object> data;

    public FinTrackerException(ErrorMessages info) {
        super(info.getMessage());
        this.info = info;
    }

    public FinTrackerException(ErrorMessages info, Throwable cause) {
        super(info.getMessage(), cause);
        this.info = info;
    }

    public FinTrackerException(ErrorMessages info, Map<String, Object> data) {
        super(info.getMessage());
        this.info = info;
        this.data = data;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(ReusableConstants.ERROR_CODE, info.getErrorCode());
        map.put(ReusableConstants.ERROR_MESSAGE, info.getMessage());
        map.put(ReusableConstants.DATA, data);
        return map;
    }

    public ErrorMessages getInfo() {
        return info;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
