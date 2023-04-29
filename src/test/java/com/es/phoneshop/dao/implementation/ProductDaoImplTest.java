package com.es.phoneshop.dao.implementation;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.Product;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ProductDaoImplTest {
    private Currency usd;

    @Mock
    private ProductDao productDao;

    @Before
    public void setup() {
        usd = Currency.getInstance("USD");
        productDao = ProductDaoImpl.getInstance();
    }

    @Test
    public void getProduct_ValidId_ShouldReturnProductFromDao() {
        // given
        final Product product = productDao.findProducts().get(0);
        final UUID id = product.getId();

        // when
        Product result = productDao.getProduct(id).get();

        // then
        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    public void findProducts_ShouldReturnProductListFromDao() {
        // given
        final List<Product> productList;

        // when
        productList = productDao.findProducts();

        // then
        assertNotNull(productList);
        assertFalse(productList.isEmpty());
    }

    @Test
    public void save_NotNullProduct_ShouldAddProductToDao() {
        // given
        final Product productToSave = new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://..");
        final int expectedSize = productDao.findProducts().size() + 1;

        // when
        productDao.save(productToSave);
        List<Product> products = productDao.findProducts();

        // then
        assertEquals(expectedSize, products.size());
        assertTrue(products.contains(productToSave));
    }

    @Test
    public void delete_ValidId_ShouldRemoveProductFromDao() {
        // given
        final Product product = new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://..");
        productDao.save(product);
        final UUID id = product.getId();

        // when
        List<Product> productList = productDao.findProducts();
        assertTrue(productList.contains(product));

        productDao.delete(id);

        // then
        productList = productDao.findProducts();
        assertFalse(productList.contains(product));
    }
}
