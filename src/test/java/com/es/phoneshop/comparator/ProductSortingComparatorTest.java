package com.es.phoneshop.comparator;

import com.es.phoneshop.model.Product;
import com.es.phoneshop.model.ProductSortCriteria;
import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class ProductSortingComparatorTest {
    private ProductSortingComparator comparator;
    private List<Product> products;

    @Before
    public void setUp() {
        Currency usd = Currency.getInstance("USD");
        products = List.of(
                new Product(UUID.randomUUID(), "simsxg75", "Siemens SXG75",
                        new BigDecimal(150), usd, 100, "https://.."),
                new Product(UUID.randomUUID(), "iphone6", "Apple iPhone 6",
                        new BigDecimal(1000), usd, 30, "https://..")
        );
    }

    @Test
    public void compare_DescriptionFieldAscOrder() {
        // given
        ProductSortCriteria sortCriteria = ProductSortCriteria.builder()
                .sortField(SortField.description)
                .sortOrder(SortOrder.asc)
                .build();
        comparator = new ProductSortingComparator(sortCriteria);

        // when
        int result = comparator.compare(products.get(0), products.get(1));

        // then
        assertTrue(result > 0);
    }

    @Test
    public void compare_PriceFieldAscOrder() {
        // given
        ProductSortCriteria sortCriteria = ProductSortCriteria.builder()
                .sortField(SortField.price)
                .sortOrder(SortOrder.asc)
                .build();
        comparator = new ProductSortingComparator(sortCriteria);

        // when
        int result = comparator.compare(products.get(0), products.get(1));

        // then
        assertTrue(result < 0);
    }

    @Test
    public void compare_PriceFieldDescOrder() {
        // given
        ProductSortCriteria sortCriteria = ProductSortCriteria.builder()
                .sortField(SortField.price)
                .sortOrder(SortOrder.desc)
                .build();
        comparator = new ProductSortingComparator(sortCriteria);

        // when
        int result = comparator.compare(products.get(0), products.get(1));

        // then
        assertTrue(result > 0);
    }

}
