package com.es.phoneshop.service.impl;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.Cart;
import com.es.phoneshop.model.CartItem;
import com.es.phoneshop.model.Product;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartServiceImplTest {
    @Mock
    private ProductService productService;
    @Mock
    private HttpSession session;
    private Product product;
    private CartService cartService;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        cartService = CartServiceImpl.getInstance();
        product = new Product(UUID.randomUUID(), "sgs2", "Samsung Galaxy S II",
                new BigDecimal(200), Currency.getInstance("USD"), 10, "https://..");

        when(session.getId()).thenReturn(UUID.randomUUID().toString());
        when(productService.getProduct(any())).thenReturn(product);

        Field productServiceField = cartService.getClass().getDeclaredField("productService");
        productServiceField.setAccessible(true);
        productServiceField.set(cartService, productService);
    }

    @Test
    public void get_HttpSessionWithExistingCart_ShouldReturnCartFromHttpSession() {
        // given
        final Cart expected = new Cart();
        when(session.getAttribute(any())).thenReturn(expected);

        // when
        Cart result = cartService.get(session);

        // then
        assertEquals(expected, result);
    }

    @Test
    public void get_HttpSessionWithNonExistingCart_ShouldAddCartToSessionAttributeAndReturnCart() {
        // given
        when(session.getAttribute(any())).thenReturn(null);

        // when
        Cart result = cartService.get(session);

        // then
        assertNotNull(result);
    }

    @Test
    public void add_NonExistingProduct_ShouldAddProductToCart() throws OutOfStockException {
        // given
        final UUID id = UUID.randomUUID();
        final Cart cart = new Cart();
        final int quantity = 1;

        // when
        cartService.add(cart, id, quantity);

        // then
        assertEquals(1, cart.getItems().size());
    }

    @Test
    public void add_ExistingProduct_ShouldAddProductToCart() throws OutOfStockException {
        // given
        final UUID id = UUID.randomUUID();
        final Cart cart = new Cart();
        final int quantityToAdd = 1;
        cart.getItems().add(new CartItem(product, quantityToAdd));

        // when
        cartService.add(cart, id, quantityToAdd);

        // then
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test(expected = OutOfStockException.class)
    public void add_SufficientStock_ShouldThrowOutOfStockException() throws OutOfStockException {
        // given
        final UUID id = UUID.randomUUID();
        final Cart cart = new Cart();
        final int quantityToAdd = 1000;
        cart.getItems().add(new CartItem(product, quantityToAdd));

        // when
        cartService.add(cart, id, quantityToAdd);

        // then (exception is thrown)
    }

    @Test(expected = IllegalArgumentException.class)
    public void add_NegativeQuantity_ShouldThrowIllegalArgumentException() throws OutOfStockException {
        // given
        final UUID id = UUID.randomUUID();
        final Cart cart = new Cart();
        final int quantityToAdd = -1000;

        // when
        cartService.add(cart, id, quantityToAdd);

        // then (exception is thrown)
    }

    @Test
    public void recalculateCart_ExistingCart_ShouldRecalculateCartAfterAddOperation() throws OutOfStockException {
        // given
        final UUID id = UUID.randomUUID();
        final Cart cart = new Cart();
        final int quantityToAdd = 1;

        // when
        cartService.add(cart, id, quantityToAdd);

        // then
        assertEquals(product.getPrice(), cart.getTotalCost());
        assertEquals(quantityToAdd, cart.getTotalQuantity());
    }

    @Test
    public void update_ValidQuantity_ShouldUpdateProductQuantity() throws OutOfStockException {
        // given
        final UUID id = UUID.randomUUID();
        final Cart cart = new Cart();
        final int updatedQuantity = 10;
        cart.getItems().add(new CartItem(product, 2));

        // when
        cartService.update(cart, id, updatedQuantity);

        // then
        assertEquals(1, cart.getItems().size());
        assertEquals(updatedQuantity, cart.getItems().get(0).getQuantity());
    }

    @Test(expected = OutOfStockException.class)
    public void update_SufficientStock_ShouldThrowOutOfStockException() throws OutOfStockException {
        // given
        final UUID id = UUID.randomUUID();
        final Cart cart = new Cart();
        final int updatedQuantity = 10_000_000;
        cart.getItems().add(new CartItem(product, 2));

        // when
        cartService.update(cart, id, updatedQuantity);

        // then (exception is thrown)
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_NegativeQuantity_ShouldThrowIllegalArgumentException() throws OutOfStockException {
        // given
        final UUID id = UUID.randomUUID();
        final Cart cart = new Cart();
        final int updatedQuantity = -1000;
        cart.getItems().add(new CartItem(product, 2));

        // when
        cartService.update(cart, id, updatedQuantity);

        // then (exception is thrown)
    }

    @Test
    public void recalculateCart_ExistingCart_ShouldRecalculateCartAfterUpdateOperation() throws OutOfStockException {
        // given
        final UUID id = UUID.randomUUID();
        final Cart cart = new Cart();
        final int updatedQuantity = 1;
        cart.getItems().add(new CartItem(product, 10));

        // when
        cartService.update(cart, id, updatedQuantity);

        // then
        assertEquals(product.getPrice(), cart.getTotalCost());
        assertEquals(1, cart.getTotalQuantity());
    }

    @Test
    public void delete_ValidId_ShouldRemoveProductFromCart() {
        // given
        final Cart cart = new Cart();
        cart.getItems().add(new CartItem(product, 1));
        final UUID id = product.getId();

        // when
        cartService.delete(cart, id);

        // then
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void clear_ValidSession_ShouldClearCart() {
        // given
        final Cart cart = new Cart();
        cart.getItems().add(new CartItem(product, 2));
        when(session.getAttribute(any())).thenReturn(cart);

        // when
        cartService.clear(session);

        // then
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void recalculateCart_ExistingCart_ShouldRecalculateCartAfterDeleteOperation() {
        // given
        final Cart cart = new Cart();
        cart.getItems().add(new CartItem(product, 1));
        final UUID id = product.getId();

        // when
        cartService.delete(cart, id);

        // then
        assertEquals(BigDecimal.ZERO, cart.getTotalCost());
        assertEquals(0, cart.getTotalQuantity());
    }

}
