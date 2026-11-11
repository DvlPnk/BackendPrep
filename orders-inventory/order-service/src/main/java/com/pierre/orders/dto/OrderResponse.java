package com.pierre.orders.dto;

import com.pierre.orders.enums.OrderStatus;

import java.time.Instant;

public record OrderResponse(
        Long id,
        Long productId,
        int quantity,
        OrderStatus status,
        Instant createdAt
) {
}
