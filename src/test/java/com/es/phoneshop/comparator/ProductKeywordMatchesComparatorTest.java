package com.es.phoneshop.comparator;

import com.es.phoneshop.model.Product;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;

public class ProductKeywordMatchesComparatorTest {
    private ProductKeywordMatchesComparator comparator;
    private Product p1;
    private Product p2;
    private Product p3;

    @Before
    public void setUp() {
        Currency usd = Currency.getInstance("USD");
        p1 = new Product(UUID.randomUUID(), "simsxg75", "Siemens SXG75",
                new BigDecimal(150), usd, 100, "https://..");
        p2 = new Product(UUID.randomUUID(), "iphone6", "Apple iPhone 6",
                new BigDecimal(1000), usd, 30, "https://..");
        p3 = new Product(UUID.randomUUID(), "sgs2", "Samsung Galaxy S II",
                new BigDecimal(200), usd, 0, "https://..");
    }

    @Test
    public void compare_ExistingKeywords_ShouldCompareProducts() {
        // given
        String[] keywords = {"Siemens", "Apple"};

        // when
        comparator = new ProductKeywordMatchesComparator(keywords);

        // then
        assertEquals(0, comparator.compare(p1, p2));
        assertEquals(0, comparator.compare(p1, p1));
        assertEquals(1, comparator.compare(p3, p1));
        assertEquals(-1, comparator.compare(p2, p3));
    }

    @Test
    public void compare_NotExistingKeywords_ShouldReturnSimilarResult() {
        // given
        String[] keywords = {"   ", "Nokia"};

        // when
        comparator = new ProductKeywordMatchesComparator(keywords);

        // then
        assertEquals(0, comparator.compare(p1, p2));
        assertEquals(0, comparator.compare(p1, p1));
        assertEquals(0, comparator.compare(p3, p1));
        assertEquals(0, comparator.compare(p2, p3));
    }

    @Test
    public void compare_EmptyKeyword_ShouldReturnSimilarResult() {
        // given
        String[] keywords = {EMPTY};

        // when
        comparator = new ProductKeywordMatchesComparator(keywords);

        // then
        assertEquals(0, comparator.compare(p1, p2));
        assertEquals(0, comparator.compare(p3, p1));
        assertEquals(0, comparator.compare(p2, p3));
    }

}
