package com.es.phoneshop.service.validator;

import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhoneNumberValidatorImplTest {
    private PhoneNumberValidatorImpl validator;

    @Before
    public void setUp() {
        validator = new PhoneNumberValidatorImpl();
    }

    @Test
    public void isValid_EmptyExpression_False() {
        // when
        boolean result = validator.isValid(EMPTY);

        // then
        assertFalse(result);
    }

    @Test
    public void isValid_InvalidSymbols_False() {
        // given
        final String expression = "375889998833";

        // when
        boolean result = validator.isValid(expression);

        // then
        assertFalse(result);
    }

    @Test
    public void isValid_InvalidLength_False() {
        // given
        final String expression = "3758899988334";

        // when
        boolean result = validator.isValid(expression);

        // then
        assertFalse(result);
    }

    @Test
    public void isValid_ValidExpression_True() {
        // given
        final String expression = "375294567890";

        // when
        boolean result = validator.isValid(expression);

        // then
        assertTrue(result);
    }

}
