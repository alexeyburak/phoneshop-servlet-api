package com.es.phoneshop.service.impl;

import com.es.phoneshop.service.Parser;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QuantityParserImplTest {
    private Locale locale;
    private Parser<Integer> parser;

    @Before
    public void setUp() {
        locale = Locale.ENGLISH;
        parser = QuantityParserImpl.getInstance();
    }

    @Test
    public void parse_ValidQuantity_ShouldReturnIntegerValue() throws ParseException {
        // given
        final String quantity = "200";

        // when
        Integer result = parser.parse(quantity, locale);

        // then
        assertNotNull(result);
        assertEquals(result, Integer.valueOf(quantity));
    }

    @Test(expected = NumberFormatException.class)
    public void parse_NotIntegerQuantity_ShouldThrowNumberFormatException() throws ParseException {
        // given
        final String quantity = "200,2";

        // when
        parser.parse(quantity, locale);

        // then (exception is thrown)
    }

    @Test(expected = NumberFormatException.class)
    public void parse_NegativeQuantity_ShouldThrowNumberFormatException() throws ParseException {
        // given
        final String quantity = "-200";

        // when
        parser.parse(quantity, locale);

        // then (exception is thrown)
    }

    @Test(expected = ParseException.class)
    public void parse_InvalidQuantity_ShouldThrowParseException() throws ParseException {
        // given
        final String quantity = "wasd";

        // when
        parser.parse(quantity, locale);

        // then (exception is thrown)
    }

}
