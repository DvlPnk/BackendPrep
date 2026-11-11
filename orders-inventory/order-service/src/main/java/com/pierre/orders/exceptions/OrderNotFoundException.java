package com.pierre.orders.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(long id){
        super("Order with id " + id + " not found");
    }
}
