package com.es.phoneshop.dao.implementation;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.ProductSortCriteria;
import com.es.phoneshop.model.Product;
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

        Field productsField = ProductDaoImpl.class.getDeclaredField("products");
        productsField.setAccessible(true);
        productsField.set(productDao, products);

        return productDao;
    }

    @Test
    public void getProduct_ValidId_ShouldReturnProductFromDao() {
        // given
        final Product product = productDao.findProducts(query, productSort).get(0);
        final UUID id = product.getId();

        // when
        Product result = productDao.getProduct(id).get();

        // then
        assertNotNull(result);
        assertEquals(product, result);
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
        final Product expectedProduct = new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://..", null);

        // when
        Product result = productDao.findProducts(query, productSort).get(0);

        // then
        assertNotNull(result);
        assertEquals(expectedProduct.getDescription(), result.getDescription());
    }

    @Test
    public void save_NotNullProduct_ShouldAddProductToDao() {
        // given
        final Product productToSave = new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://..", null);
        final int expectedSize = productDao.findProducts(query, productSort).size() + 1;

        // when
        productDao.save(productToSave);
        List<Product> products = productDao.findProducts(query, productSort);

        // then
        assertEquals(expectedSize, products.size());
        assertTrue(products.contains(productToSave));
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
