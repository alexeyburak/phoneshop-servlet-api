package com.es.phoneshop.exception;

public class OutOfStockException extends Exception {
    public OutOfStockException() {
    }

    public OutOfStockException(String message) {
        super(message);
    }
}
