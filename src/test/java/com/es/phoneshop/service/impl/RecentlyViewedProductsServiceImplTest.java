package com.es.phoneshop.service.impl;

import com.es.phoneshop.model.Product;
import com.es.phoneshop.model.RecentlyViewedProductUnit;
import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.RecentlyViewedProductsService;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecentlyViewedProductsServiceImplTest {
    @Mock
    private ProductService productService;
    @Mock
    private HttpSession session;
    @Mock
    private RecentlyViewedProductUnit recentlyViewedProductUnit;
    private Product product;
    private RecentlyViewedProductsService recentlyViewedService;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        recentlyViewedService = RecentlyViewedProductsServiceImpl.getInstance();
        product = new Product(UUID.randomUUID(), "sgs2", "Samsung Galaxy S II",
                new BigDecimal(200), Currency.getInstance("USD"), 10, "https://..");

        when(session.getAttribute(any())).thenReturn(recentlyViewedProductUnit);
        when(session.getId()).thenReturn(UUID.randomUUID().toString());
        when(productService.getProduct(any())).thenReturn(product);

        Field productServiceField = recentlyViewedService.getClass().getDeclaredField("productService");
        productServiceField.setAccessible(true);
        productServiceField.set(recentlyViewedService, productService);
    }

    @Test
    public void get_HttpSessionWithExistingUnit_ShouldReturnRecentlyViewedProductUnitFromSession() {
        // given
        Set<Product> products = Set.of(
                new Product(UUID.randomUUID(), "sgs2", "Samsung Galaxy S II", new BigDecimal(200),
                        Currency.getInstance("USD"), 0, "https://..")
        );
        when(recentlyViewedProductUnit.getProducts()).thenReturn(products);

        // when
        RecentlyViewedProductUnit result = recentlyViewedService.get(session);

        // then
        assertEquals(products, result.getProducts());
    }

    @Test
    public void get_HttpSessionWithNonExistingUnit_ShouldReturnRecentlyViewedProductUnitFromSession() {
        // when
        RecentlyViewedProductUnit result = recentlyViewedService.get(session);

        // then
        assertNotNull(result);
    }

    @Test
    public void add_EmptyProductUnit_ShouldAddProductToUnit() {
        // given
        final UUID id = UUID.randomUUID();
        Set<Product> products = new LinkedHashSet<>();
        when(recentlyViewedProductUnit.getProducts()).thenReturn(products);

        // when
        recentlyViewedService.add(id, recentlyViewedProductUnit);

        // then
        assertEquals(1, products.size());
    }

    @Test
    public void add_ExistingProduct_ShouldRemoveExistingProductAndAddProductToUnit() {
        // given
        final UUID id = UUID.randomUUID();
        Set<Product> products = new LinkedHashSet<>();
        products.add(product);
        when(recentlyViewedProductUnit.getProducts()).thenReturn(products);

        // when
        recentlyViewedService.add(id, recentlyViewedProductUnit);

        // then
        assertEquals(1, products.size());
    }

    @Test
    public void add_ProductUnitWithMaxSize_ShouldRemoveOldProductAndAddNewProductToUnit() {
        // given
        final UUID id = UUID.randomUUID();
        Set<Product> products = new LinkedHashSet<>();
        products.add(new Product(UUID.randomUUID(), "sgs2", "Samsung Galaxy S II",
                new BigDecimal(200), null, 0, "https://.."));
        products.add(new Product(UUID.randomUUID(), "simsxg75", "Siemens SXG75",
                new BigDecimal(150), null, 100, "https://.."));
        products.add(new Product(UUID.randomUUID(), "sec901", "Sony Ericsson C901",
                new BigDecimal(420), null, 30, "https://.."));
        when(recentlyViewedProductUnit.getProducts()).thenReturn(products);

        // when
        recentlyViewedService.add(id, recentlyViewedProductUnit);

        // then
        assertEquals(3, products.size());
        assertTrue(products.contains(product));
    }

}
