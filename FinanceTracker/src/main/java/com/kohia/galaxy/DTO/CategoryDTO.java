package com.kohia.galaxy.DTO;

public record CategoryDTO(
        Long categoryId,
        String name,
        String description,
        Long monthlyLimit
) { }
