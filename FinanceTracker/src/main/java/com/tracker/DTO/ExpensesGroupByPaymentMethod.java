package com.tracker.DTO;

import com.tracker.entity.PaymentMethod;

import java.util.List;

public record ExpensesGroupByPaymentMethod(
        PaymentMethod paymentMethod,
        List<ExpenseDTO> expenses,
        Double expenseSum
) { }