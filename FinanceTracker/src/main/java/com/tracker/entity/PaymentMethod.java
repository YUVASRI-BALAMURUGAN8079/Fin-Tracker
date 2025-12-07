package com.tracker.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PaymentMethod")
@Getter
@Setter
public class PaymentMethod {
    @Id
    @Column(name = "paymentMethodId")
    private Long paymentMethodId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "createdTime")
    private LocalDateTime createdTime;

    @Column(name = "updatedTime")
    private LocalDateTime updatedTime;

    @Column(name = "deletedTime")
    private Long deletedTime = 0L;
}
