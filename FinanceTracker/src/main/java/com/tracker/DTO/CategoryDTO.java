package com.tracker.DTO;

public record CategoryDTO(
        Long categoryId,
        String name,
        String description,
        Long monthlyLimit
) { }
