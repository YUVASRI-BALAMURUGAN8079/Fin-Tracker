package com.kohia.galaxy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "Category")
@Getter
@Setter
public class Category {
    @Id
    @Column(name = "categoryId")
    private Long categoryId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", nullable = false)
    private String description = "";

    @Column(name = "deletedTime")
    private Long deletedTime = 0L;

    @Column(name = "createdTime")
    private LocalDateTime createdTime;

    @Column(name = "updatedTime")
    private LocalDateTime updatedTime;

    @Column(name = "currentMonthlyLimit")
    private Long currentMonthlyLimit;

}
