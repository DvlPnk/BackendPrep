package com.pierre.inventory.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateProductActiveRequest(
        @NotNull(message = "Active status must be provided")
        Boolean active
) {
}
