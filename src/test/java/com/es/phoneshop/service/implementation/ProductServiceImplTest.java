package com.es.phoneshop.service.implementation;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.implementation.ProductDaoImpl;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.Product;
import com.es.phoneshop.service.ProductService;
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

public class ProductServiceImplTest {
    private ProductService productService;
    private Currency usd;

    @Mock
    private ProductDao productDao;

    @Before
    public void setup() {
        usd = Currency.getInstance("USD");
        productService = new ProductServiceImpl();
        productDao = ProductDaoImpl.getInstance();
    }

    @Test
    public void getProduct_ValidId_ShouldReturnProductWithIdFromDao() {
        // given
        final UUID id = productService.findProducts().get(0).getId();
        final BigDecimal expectedPrice = new BigDecimal(100);
        final String expectedDescription = "Samsung Galaxy S";
        final int expectedStock = 100;

        // when
        Product product = productService.getProduct(id);

        // then
        assertNotNull(product);
        assertEquals(expectedPrice, product.getPrice());
        assertEquals(expectedDescription, product.getDescription());
        assertEquals(expectedStock, product.getStock());
    }

    @Test(expected = ProductNotFoundException.class)
    public void getProduct_InvalidId_ShouldThrowProductNotFoundException() {
        // given
        final UUID id = UUID.randomUUID();

        // when
        productService.getProduct(id);

        // then (exception is thrown)
    }

    @Test(expected = IllegalArgumentException.class)
    public void getProduct_NullId_ShouldThrowIllegalArgumentException() {
        // when
        productService.getProduct(null);

        // then (exception is thrown)
    }

    @Test
    public void findProducts_ShouldReturnProductListFromDao() {
        // when
        List<Product> products = productService.findProducts();

        // then
        assertFalse(products.isEmpty());
    }

    @Test
    public void findProducts_ProductWithZeroStock_ShouldNotIncludeProductToList() {
        // given
        final int stock = 0;
        final Product productWithZeroStock = new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, stock, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.save(productWithZeroStock);

        // when
        List<Product> result = productService.findProducts();

        // then
        assertTrue(result.stream()
                .noneMatch(product -> product.getStock() == stock));
    }

    @Test
    public void findProducts_ProductWithNegativeStock_ShouldNotIncludeProductToList() {
        // given
        final int stock = -1;
        final Product productWithZeroStock = new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, stock, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.save(productWithZeroStock);

        // when
        List<Product> result = productService.findProducts();

        // then
        assertTrue(result.stream()
                .noneMatch(product -> product.getStock() == stock));
    }

    @Test
    public void testNotShowingProductWithNullPrice() {
        // given
        final Product productWithNullPrice = new Product("simsxg75", "Siemens SXG75", null, usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.save(productWithNullPrice);

        // when
        List<Product> result = productService.findProducts();

        // then
        assertTrue(result.stream()
                .noneMatch(product -> product.getPrice() == null));
    }

    @Test
    public void save_ShouldSaveProductToDao() {
        // given
        final Product product = new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg");

        // when
        productService.save(product);
        UUID id = product.getId();

        // then
        assertEquals(product, productService.getProduct(id));
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_NullId_ShouldThrowIllegalArgumentException() {
        // when
        productService.delete(null);

        // then (exception is thrown)
    }

    @Test(expected = ProductNotFoundException.class)
    public void delete_ValidId_ShouldThrowProductNotFoundException() {
        // given
        final Product product = new Product("simsxg75", "Siemens SXG75", null, usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        productDao.save(product);
        final UUID id = product.getId();

        // when
        productService.delete(id);

        // then (exception is thrown)
        productService.getProduct(id);
    }

}
