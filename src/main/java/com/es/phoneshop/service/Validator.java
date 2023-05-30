package com.es.phoneshop.service;

@FunctionalInterface
public interface Validator<T> {
    boolean isValid(T expression);
}
