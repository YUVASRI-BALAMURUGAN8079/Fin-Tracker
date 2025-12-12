package com.tracker.DTO;

import com.tracker.entity.Category;

import java.util.List;

public record ExpensesGroupByCategory(
        Category category,
        List<ExpenseDTO> expenses,
        Double expenseSum,
        Double percentSpent
) { }