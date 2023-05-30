package com.es.phoneshop.service.validator;

import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EmptyValidatorImplTest {
    private EmptyValidatorImpl validator;

    @Before
    public void setUp() {
        validator = new EmptyValidatorImpl();
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
    public void isValid_ValidExpression_True() {
        // given
        final String expression = "expression";

        // when
        boolean result = validator.isValid(expression);

        // then
        assertTrue(result);
    }

}
