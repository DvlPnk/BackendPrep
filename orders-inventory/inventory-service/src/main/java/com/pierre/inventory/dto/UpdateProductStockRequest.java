package com.pierre.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateProductStockRequest (
        @NotNull(message = "Quantity is missing")
        @Positive(message = "Quantity is zero, or negative")
        Integer quantity
){
}
