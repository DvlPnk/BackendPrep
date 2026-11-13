package com.pierre.inventory.exception;

public class StockDeductionException extends RuntimeException{
    private final String code;

    public StockDeductionException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
