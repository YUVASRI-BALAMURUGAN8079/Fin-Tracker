package com.tracker.DTO;

import java.time.LocalDate;
import java.util.List;

public record BillDTO(
        Long billId,
        CategoryDTO category,
        String name,
        Double amount,
        Boolean isRecurring,
        LocalDate dueDate,
        Integer reminderBeforeDays,
        Boolean autoCreateExpense,
        String frequency,
        Boolean paid,
        List<ExpenseDTO> expenses,
        LocalDate reminderDate
) {
}