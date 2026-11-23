package com.pierre.inventory.contracts;

public enum InventoryErrorCode {
    PRODUCT_NOT_FOUND,
    PRODUCT_INACTIVE,
    INSUFFICIENT_STOCK,
    IDEMPOTENCY_KEY_MISMATCH,
    OPTIMISTIC_LOCK_CONFLICT
}