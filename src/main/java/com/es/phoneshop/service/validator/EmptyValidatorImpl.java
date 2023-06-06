package com.es.phoneshop.service.validator;

import com.es.phoneshop.service.Validator;

public class EmptyValidatorImpl implements Validator<String> {

    @Override
    public boolean isValid(String expression) {
        return expression != null && !expression.isEmpty();
    }

}
