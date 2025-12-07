package com.tracker.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FinTrackerException.class)
    public ResponseEntity<Map<String, Object>> handleGalaxyException(FinTrackerException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put(ReusableConstants.ERROR_CODE, ex.getInfo().getErrorCode());
        response.put(ReusableConstants.ERROR_MESSAGE, ex.getInfo().getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
