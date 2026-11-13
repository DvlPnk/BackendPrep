package com.pierre.inventory.exception;

public class IdempotencyKeyMismatchException extends RuntimeException{
    public IdempotencyKeyMismatchException(long orderId){
        super("Order " + orderId
                + " already has a deduction with a different product or quantity");
    }
}
