package com.kohia.galaxy.DTO;

import com.kohia.galaxy.entity.Category;
import com.kohia.galaxy.entity.PaymentMethod;

import java.time.LocalDate;

public record ExpenseDTO(
        Long expenseTransactionsId,
        Category category,
        PaymentMethod paymentMethod,
        Long amount,
        String description,
        LocalDate date
) { }
