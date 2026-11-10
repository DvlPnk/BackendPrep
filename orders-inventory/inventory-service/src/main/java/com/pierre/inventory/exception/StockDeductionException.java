package com.pierre.inventory.exception;

public class StockDeductionException extends RuntimeException{
    public StockDeductionException(String message){
        super(message);
    }
}
