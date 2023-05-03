package com.es.phoneshop.service;

/**
 * Represents an operation that does not return a result.
 *
 * <p> This is a functional interface
 * whose functional method is {@link #get()}.
 */
@FunctionalInterface
public interface Procedure {
    /**
     * Runs this operation.
     */
    void get();
}
