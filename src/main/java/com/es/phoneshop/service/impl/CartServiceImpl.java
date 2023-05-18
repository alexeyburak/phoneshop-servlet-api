package com.es.phoneshop.service.impl;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.Cart;
import com.es.phoneshop.model.CartItem;
import com.es.phoneshop.model.Product;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ProductService;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;
import java.util.UUID;

public class CartServiceImpl implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = CartServiceImpl.class.getName() + ".cart";
    private final ProductService productService;

    private CartServiceImpl() {
        this.productService = ProductServiceImpl.getInstance();
    }

    private static final class SingletonHolder {
        private static final CartServiceImpl INSTANCE = new CartServiceImpl();
    }

    public static CartServiceImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public Cart get(HttpSession session) {
        synchronized (session.getId().intern()) {
            Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                cart = new Cart();
                session.setAttribute(CART_SESSION_ATTRIBUTE, cart);
            }

            return cart;
        }
    }

    @Override
    public void add(Cart cart, UUID productId, int quantity) throws OutOfStockException {
        Product product = productService.getProduct(productId);

        synchronized (cart) {
            Optional<CartItem> cartItem = getCartItemByProduct(product, cart);

            if (cartItem.isPresent()) {
                addExistingProduct(quantity, product, cartItem.get());
            } else {
                addNonExistingProduct(quantity, product, cart);
            }
        }
    }

    private Optional<CartItem> getCartItemByProduct(Product product, Cart cart) {
        return cart.getItems()
                .stream()
                .filter(item -> product.equals(item.getProduct()))
                .findAny();
    }

    private void addExistingProduct(int quantity, Product product, CartItem cartItem) throws OutOfStockException {
        int newQuantity = cartItem.getQuantity() + quantity;
        validateIfSufficientStock(product, newQuantity);
        cartItem.setQuantity(newQuantity);
    }

    private void addNonExistingProduct(int quantity, Product product, Cart cart) throws OutOfStockException {
        validateIfSufficientStock(product, quantity);
        cart.getItems().add(new CartItem(product, quantity));
    }

    private void validateIfSufficientStock(Product product, int quantity) throws OutOfStockException {
        if (product.getStock() < quantity) {
            throw new OutOfStockException();
        }
    }

}
