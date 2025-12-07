package com.kohia.galaxy.DTO;

import com.kohia.galaxy.entity.Category;

import java.util.List;

public record ExpensesGroupByCategory(
        Category category,
        List<ExpenseDTO> expenses,
        Long expenseSum,
        Long percentSpent
) { }