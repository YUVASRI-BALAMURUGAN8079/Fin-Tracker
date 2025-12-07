package com.tracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;

@Entity
@Table
@Getter
@Setter
public class ExpenseTransactions {
    @Id
    @Column(name = "expenseTransactionsId")
    private Long expenseTransactionsId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category = null;

    @ManyToOne
    @JoinColumn(name = "paymentMethodId")
    private PaymentMethod paymentMethod = null;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "description")
    private String description;
}
