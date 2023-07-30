package com.mypill.domain.cart.exception;

public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String msg) {
        super(msg);
    }
}