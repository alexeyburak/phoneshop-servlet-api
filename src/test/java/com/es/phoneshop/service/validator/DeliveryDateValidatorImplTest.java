package com.es.phoneshop.service.validator;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeliveryDateValidatorImplTest {
    private DeliveryDateValidatorImpl validator;

    @Before
    public void setUp() {
        validator = new DeliveryDateValidatorImpl();
    }

    @Test
    public void isValid_NullExpression_False() {
        // when
        boolean result = validator.isValid(null);

        // then
        assertFalse(result);
    }

    @Test
    public void isValid_EmptyExpression_False() {
        // when
        boolean result = validator.isValid(EMPTY);

        // then
        assertFalse(result);
    }

    @Test
    public void isValid_DateIsBeforeNow_False() {
        // given
        final String date = LocalDate.now()
                .minusDays(1).toString();

        // when
        boolean result = validator.isValid(date);

        // then
        assertFalse(result);
    }

    @Test
    public void isValid_DateIsAfterMoreThenOneMonth_False() {
        // given
        final String date = LocalDate.now()
                .plusMonths(1).toString();

        // when
        boolean result = validator.isValid(date);

        // then
        assertFalse(result);
    }

    @Test
    public void isValid_ValidExpression_True() {
        // given
        final String date = LocalDate.now()
                .plusDays(1).toString();

        // when
        boolean result = validator.isValid(date);

        // then
        assertTrue(result);
    }

}
