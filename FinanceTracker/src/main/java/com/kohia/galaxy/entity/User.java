package com.kohia.galaxy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "User")
public class User {
    @Id
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String password;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column
    private LocalDateTime updatedTime;

    @Column
    private String timeZone = "Asia/Kolkata";

    public User(){}

    public User(Long userId,String name, String email, String password, Boolean isActive, LocalDateTime createdTime) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.createdTime = createdTime;
    }

    public String getPassword() { return password; }

    public Long getUserId() { return userId; }

    public String getTimeZone() { return timeZone; }
}
