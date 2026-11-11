package com.pierre.orders.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
    @NotNull(message = "Product id must be provided")
    @Positive(message = "Product id must be greater than zero")
    Long productId,

    @NotNull(message = "Quantity must be provided")
    @Positive(message = "Quantity must be greater than zero")
    Integer quantity
) {
}
