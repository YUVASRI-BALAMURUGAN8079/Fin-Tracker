package com.tracker.pojo;

import lombok.Getter;

@Getter
public enum SQLConstraintErrorConstant {
    USER_EMAIL_UK("User.email");

    private final String key;

    SQLConstraintErrorConstant(String key) {
        this.key = key;
    }

}