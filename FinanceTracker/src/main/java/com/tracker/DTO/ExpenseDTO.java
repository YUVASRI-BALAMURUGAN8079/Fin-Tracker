package com.tracker.DTO;

import com.tracker.entity.Category;
import com.tracker.entity.PaymentMethod;

import java.time.LocalDate;

public record ExpenseDTO(
        Long expenseTransactionsId,
        Category category,
        PaymentMethod paymentMethod,
        Long amount,
        String description,
        LocalDate date
) { }
