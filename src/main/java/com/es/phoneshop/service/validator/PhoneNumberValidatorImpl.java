package com.es.phoneshop.service.validator;

import com.es.phoneshop.service.Validator;

import java.util.regex.Pattern;

public class PhoneNumberValidatorImpl implements Validator<String> {
    private static final String PHONE_REGEX = "^375(33|29|44)\\d{7}$";

    @Override
    public boolean isValid(String expression) {
        return Pattern.matches(PHONE_REGEX, expression);
    }

}
