package com.tracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SavingsGoalTransaction")
@Getter
@Setter
public class SavingsGoalTransaction {
    @Id
    private Long txnId;

    @Column(nullable = false)
    private Long userId;

    private Double amount;
    private LocalDate txnDate;
    private String note;

    @ManyToOne
    @JoinColumn(name = "goalId")
    private SavingsGoal savingsGoal;

    private Long deletedTime = 0L;
}
