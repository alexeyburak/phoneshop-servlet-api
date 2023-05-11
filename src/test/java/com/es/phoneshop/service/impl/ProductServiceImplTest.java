package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.ProductSortCriteria;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.Product;
import com.es.phoneshop.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {
    private ProductService productService;
    private Currency usd;
    private String query;
    private ProductSortCriteria sortCriteria;

    @Mock
    private ProductDao productDao;

    @Before
    public void setup() {
        usd = Currency.getInstance("USD");
        query = EMPTY;
        sortCriteria = null;
        productService = new ProductServiceImpl(productDao);
    }

    @Test
    public void getProduct_ValidId_ShouldReturnProductWithIdFromDao() {
        // given
        final Product expectedProduct = new Product(UUID.randomUUID(), "sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://..");
        final UUID id = expectedProduct.getId();
        when(productDao.getProduct(id)).thenReturn(Optional.of(expectedProduct));

        // when
        Product result = productService.getProduct(id);

        // then
        verify(productDao, times(1)).getProduct(id);
        assertEquals(expectedProduct, result);
    }

    @Test(expected = ProductNotFoundException.class)
    public void getProduct_InvalidId_ShouldThrowProductNotFoundException() {
        // given
        final UUID id = UUID.randomUUID();

        // when
        productService.getProduct(id);

        // then (exception is thrown)
    }

    @Test(expected = NullPointerException.class)
    public void getProduct_NullId_ShouldThrowIllegalArgumentException() {
        // when
        productService.getProduct(null);

        // then (exception is thrown)
    }

    @Test
    public void findProducts_ShouldReturnProductListFromDao() {
        // given
        List<Product> expectedProducts = Arrays.asList(
                new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 100, "https://..", null),
                new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(150), usd, 100, "https://..", null)
        );
        when(productDao.findProducts(query, sortCriteria)).thenReturn(expectedProducts);

        // when
        List<Product> result = productService.findProducts(query, sortCriteria);

        // then
        verify(productDao, times(1)).findProducts(query, sortCriteria);
        assertEquals(expectedProducts, result);
    }

    @Test
    public void findProducts_ProductWithZeroStock_ShouldNotIncludeProductToList() {
        // given
        final int stock = 0;
        List<Product> expectedProducts = Arrays.asList(
                new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, stock, "https://..", null),
                new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(150), usd, 100, "https://..", null)
        );
        when(productDao.findProducts(query, sortCriteria)).thenReturn(expectedProducts);

        // when
        List<Product> result = productService.findProducts(query, sortCriteria);

        // then
        assertTrue(result.stream()
                .noneMatch(product -> product.getStock() == stock));
    }

    @Test
    public void findProducts_ProductWithNegativeStock_ShouldNotIncludeProductToList() {
        // given
        final int stock = -1;
        List<Product> expectedProducts = Arrays.asList(
                new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, stock, "https://..", null),
                new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(150), usd, 100, "https://..", null)
        );
        when(productDao.findProducts(query, sortCriteria)).thenReturn(expectedProducts);

        // when
        List<Product> result = productService.findProducts(query, sortCriteria);

        // then
        assertTrue(result.stream()
                .noneMatch(product -> product.getStock() == stock));
    }

    @Test
    public void findProducts_ProductWithNullPrice_ShouldNotIncludeProductToList() {
        // given
        List<Product> expectedProducts = Arrays.asList(
                new Product("simsxg75", "Siemens SXG75", null, usd, 100, "https://..", null),
                new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(150), usd, 100, "https://..", null)
        );
        when(productDao.findProducts(query, sortCriteria)).thenReturn(expectedProducts);

        // when
        List<Product> result = productService.findProducts(query, sortCriteria);

        // then
        assertTrue(result.stream()
                .noneMatch(product -> product.getPrice() == null));
    }

    @Test
    public void save_ShouldSaveProductToDao() {
        // given
        final Product product = new Product(UUID.randomUUID(), "sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://..");
        doNothing().when(productDao).save(product);

        // when
        productService.save(product);

        // then
        verify(productDao, times(1)).save(product);
    }

    @Test(expected = NullPointerException.class)
    public void delete_NullId_ShouldThrowIllegalArgumentException() {
        // when
        productService.delete(null);

        // then (exception is thrown)
    }

    @Test
    public void delete_ValidId_ShouldRemoveProductFromDao() {
        // given
        final UUID id = UUID.randomUUID();
        doNothing().when(productDao).delete(id);

        // when
        productService.delete(id);

        // then
        verify(productDao, times(1)).delete(id);
    }

}
