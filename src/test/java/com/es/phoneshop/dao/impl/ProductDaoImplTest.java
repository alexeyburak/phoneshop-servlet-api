package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.ProductSearchCriteria;
import com.es.phoneshop.model.ProductSortCriteria;
import com.es.phoneshop.model.Product;
import com.es.phoneshop.model.enums.SearchMethod;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ProductDaoImplTest {
    private Currency usd;
    private String query;
    private ProductSortCriteria productSort;

    private ProductDao productDao;

    @Before
    public void setup() {
        usd = Currency.getInstance("USD");
        query = EMPTY;
        productSort = null;

        productDao = createProductDaoWithProducts(new ArrayList<>(
                List.of(
                        new Product(UUID.randomUUID(), "simsxg75", "Siemens SXG75",
                                new BigDecimal(150), usd, 100, "https://.."),
                        new Product(UUID.randomUUID(), "sgs2", "Samsung Galaxy S II",
                                new BigDecimal(150), usd, 100, "https://.."),
                        new Product(UUID.randomUUID(), "sgs3", "Samsung Galaxy S III",
                                new BigDecimal(300), usd, 5, "https://.."),
                        new Product(UUID.randomUUID(), "sec901", "Sony Ericsson C901",
                                new BigDecimal(420), usd, 30, "https://..")
                ))
        );
    }

    @SneakyThrows
    private ProductDaoImpl createProductDaoWithProducts(final List<Product> products) {
        ProductDaoImpl productDao;

        Constructor<ProductDaoImpl> constructor = ProductDaoImpl.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        productDao = constructor.newInstance();

        Field productsField = AbstractGenericDao.class.getDeclaredField("items");
        productsField.setAccessible(true);
        productsField.set(productDao, products);

        return productDao;
    }

    @Test
    public void getInstance_ShouldCreateAndReturnOneSameInstance() {
        // given
        ProductDaoImpl instance1;
        ProductDaoImpl instance2;

        // when
        instance1 = ProductDaoImpl.getInstance();
        instance2 = ProductDaoImpl.getInstance();

        // then
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertEquals(instance1, instance2);
    }

    @Test
    public void getProduct_ValidId_ShouldReturnProductFromDao() {
        // given
        final Product product = productDao.findProducts(query, productSort).get(0);
        final UUID id = product.getId();

        // when
        Product result = productDao.get(id).get();

        // then
        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    public void findProducts_WithNoCriteria_ShouldReturnProductListFromDao() {
        // given
        final List<Product> productList;

        // when
        productList = productDao.findProducts();

        // then
        assertNotNull(productList);
        assertFalse(productList.isEmpty());
    }

    @Test
    public void findProducts_NullParameters_ShouldReturnProductListFromDao() {
        // given
        final List<Product> productList;

        // when
        productList = productDao.findProducts(query, productSort);

        // then
        assertNotNull(productList);
        assertFalse(productList.isEmpty());
    }

    @Test
    public void findProducts_QueryParameters_ShouldReturnProductListFromDaoWithSearchedQuery() {
        // given
        final String query = "samsung iii";
        final Product expectedProduct = new Product("sgs3", "Samsung Galaxy S III",
                new BigDecimal(300), usd, 5, "https://..", null);

        // when
        Product result = productDao.findProducts(query, productSort).get(0);

        // then
        assertNotNull(result);
        assertEquals(expectedProduct.getDescription(), result.getDescription());
    }

    @Test
    public void findProducts_SearchCriteria_ShouldReturnProductListFromDaoWithSearchCriteria() {
        // given
        final long expectedProductPrice = 420L;
        final ProductSearchCriteria criteria = new ProductSearchCriteria(BigDecimal.valueOf(expectedProductPrice),
                BigDecimal.valueOf(expectedProductPrice), SearchMethod.ANY_WORD);
        final Product expectedProduct = new Product(UUID.randomUUID(), "sec901", "Sony Ericsson C901",
                new BigDecimal(expectedProductPrice), usd, 30, "https://..");

        // when
        Product result = productDao.findProducts(query, criteria).get(0);

        // then
        assertNotNull(result);
        assertEquals(expectedProduct.getDescription(), result.getDescription());
    }

    @Test
    public void save_NotNullProduct_ShouldAddProductToDao() {
        // given
        final Product productToSave = new Product("simsxg75", "Siemens SXG75",
                new BigDecimal(150), usd, 40, "https://..", null);
        final int expectedSize = productDao.findProducts(query, productSort).size() + 1;

        // when
        productDao.save(productToSave);
        List<Product> products = productDao.findProducts(query, productSort);

        // then
        assertEquals(expectedSize, products.size());
        assertTrue(products.contains(productToSave));
    }

    @Test(expected = NullPointerException.class)
    public void save_NullProduct_ShouldThrowNullPointerException() {
        // given
        final Product productToSave = null;

        // when
        productDao.save(productToSave);

        // then (exception is thrown)
    }

    @Test
    public void delete_ValidId_ShouldRemoveProductFromDao() {
        // given
        final Product product = productDao.findProducts(query, productSort).get(0);
        final UUID id = product.getId();

        // when
        productDao.delete(id);
        List<Product> result = productDao.findProducts(query, productSort);

        // then
        assertFalse(result.contains(product));
    }
}
