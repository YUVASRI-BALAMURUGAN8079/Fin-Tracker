package com.kohia.galaxy.pojo;

public enum SQLConstraint {
    USER_EMAIL_UK("User.email");

    private final String key;

    SQLConstraint(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}