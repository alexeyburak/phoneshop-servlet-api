package com.es.phoneshop.service.impl;

import com.es.phoneshop.service.Parser;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Pattern;

public class QuantityParserImpl implements Parser<Integer> {

    private QuantityParserImpl() {
    }

    private static final class SingletonHolder {
        private static final QuantityParserImpl INSTANCE = new QuantityParserImpl();
    }

    public static QuantityParserImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public Integer parse(String quantity, Locale locale) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(locale);
        Number parsingQuantity = format.parse(quantity);
        validateInteger(quantity);
        return parsingQuantity.intValue();
    }

    private void validateInteger(String value) {
        String DIGIT_REGEX = "^\\d+$";
        if (!Pattern.matches(DIGIT_REGEX, value)) {
            throw new NumberFormatException();
        }
    }

}
