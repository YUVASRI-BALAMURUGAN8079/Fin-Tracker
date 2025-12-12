package com.tracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "SavingsGoal")
@Getter
@Setter
public class SavingsGoal {
    @Id
    private Long goalId;

    private Long userId;
    private String name;
    private Double targetAmount;
    private Double currentAmount = 0.0;

    private LocalDate targetDate;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Long deletedTime = 0L;

    @OneToMany(mappedBy = "savingsGoal", cascade = CascadeType.ALL)
    private List<SavingsGoalTransaction> transactions;
}
