package com.pierre.inventory.dto;

import java.util.List;

public record ProductPageResponse(
        List<ProductResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
