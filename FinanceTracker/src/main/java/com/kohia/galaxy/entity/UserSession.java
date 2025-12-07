package com.kohia.galaxy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class UserSession {

    @Id
    private Long sessionId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "csrfToken", nullable = false)
    private String csrfToken;

    @Column(name = "createdTime")
    private LocalDateTime createdTime;

    @Column(name = "expiresAt")
    private LocalDateTime expiresAt;

    public UserSession() {}

    public UserSession(Long sessionId, Long userId, String csrfToken, LocalDateTime createdTime, LocalDateTime expiresAt) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.csrfToken = csrfToken;
        this.createdTime = createdTime;
        this.expiresAt = expiresAt;
    }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCsrfToken() { return csrfToken; }
    public void setCsrfToken(String csrfToken) { this.csrfToken = csrfToken; }

    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getExpiryTime() {
        return expiresAt;
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

}
