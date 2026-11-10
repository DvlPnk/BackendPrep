package com.pierre.inventory.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "Product name must be not blank")
        @Size(max = 150)
        String name,

        @NotNull(message = "Product price must be provided")
        @DecimalMin(value = "0.00", message = "Product price must be zero or greater")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @NotNull(message = "Product stock must be provided")
        @Min(value = 0, message = "Product stock must be zero or greater")
        Integer stock
) {
}
