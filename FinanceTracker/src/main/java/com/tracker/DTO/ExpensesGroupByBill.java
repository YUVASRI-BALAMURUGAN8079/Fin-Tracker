package com.tracker.DTO;

import com.tracker.entity.Bills;

import java.util.List;

public record ExpensesGroupByBill(
        Bills bill,
        List<ExpenseDTO> expenses,
        Double expenseSum
) { }