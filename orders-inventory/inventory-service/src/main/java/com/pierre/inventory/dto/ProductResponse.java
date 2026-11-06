package com.pierre.inventory.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        int stock,
        boolean active
) {
}
