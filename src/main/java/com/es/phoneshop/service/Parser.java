package com.es.phoneshop.service;

import java.text.ParseException;
import java.util.Locale;

@FunctionalInterface
public interface Parser<T> {
    T parse(String quantity, Locale locale) throws ParseException;
}
