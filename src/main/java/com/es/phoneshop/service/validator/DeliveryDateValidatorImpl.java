package com.es.phoneshop.service.validator;

import com.es.phoneshop.service.Validator;

import java.time.LocalDate;

public class DeliveryDateValidatorImpl implements Validator<String> {
    private static final int VALID_PERIOD_OF_MONTH = 1;

    @Override
    public boolean isValid(String expression) {
        if (expression == null || expression.isEmpty()) {
            return false;
        }
        LocalDate localDate = LocalDate.parse(expression);

        return localDate.isAfter(LocalDate.now()) &&
                localDate.isBefore(LocalDate.now().plusMonths(VALID_PERIOD_OF_MONTH));
    }

}
