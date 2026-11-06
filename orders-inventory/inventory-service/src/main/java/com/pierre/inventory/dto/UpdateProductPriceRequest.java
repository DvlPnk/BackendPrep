package com.pierre.inventory.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateProductPriceRequest (
        @NotNull(message = "Product price must be provided")
        @DecimalMin(
                value = "0.00",
                message = "Product price must be zero or greater"
        )
        @Digits(integer = 10, fraction = 2)
        BigDecimal price
){
}
