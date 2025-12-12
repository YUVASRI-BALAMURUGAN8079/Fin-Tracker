package com.tracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "Bills")
@Getter
@Setter
public class Bills {

    @Id
    private Long billId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private boolean paid = false;

    @Column(nullable = false)
    private boolean recurring = false;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    public enum Frequency {
        WEEKLY,MONTHLY, QUARTERLY,HALF_YEARLY, YEARLY, NONE
    }

    @Column(nullable = false)
    private Integer reminderBeforeDays = 3;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @Column(nullable = false)
    private Boolean autoCreateExpense = false;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column
    private LocalDateTime updatedTime;

    @Column(name = "deletedTime")
    private Long deletedTime = 0L;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExpenseTransactions> transactions;
}
