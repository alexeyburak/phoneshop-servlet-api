package com.es.phoneshop.service.impl;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.Cart;
import com.es.phoneshop.model.CartItem;
import com.es.phoneshop.model.Product;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CartServiceImpl implements CartService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CartServiceImpl.class);
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
        String sessionId = session.getId();

        synchronized (sessionId.intern()) {
            Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                cart = new Cart();
                session.setAttribute(CART_SESSION_ATTRIBUTE, cart);
            }

            LOGGER.debug("Get cart from session. Session sessionId: {}", sessionId);
            return cart;
        }
    }

    @Override
    public void add(Cart cart, UUID productId, int quantity) throws OutOfStockException {
        Product product = productService.getProduct(productId);
        validateIfNegativeQuantity(quantity);

        synchronized (cart) {
            Optional<CartItem> cartItem = getCartItemByProduct(product, cart);

            if (cartItem.isPresent()) {
                addExistingProduct(quantity, product, cartItem.get());
            } else {
                addNonExistingProduct(quantity, product, cart);
            }

            LOGGER.debug("Add product to cart. Product id: {}", productId);
            recalculateCart(cart);
        }
    }

    @Override
    public void update(Cart cart, UUID productId, int quantity) throws OutOfStockException {
        Product product = productService.getProduct(productId);
        validateIfNegativeQuantity(quantity);

        synchronized (cart) {
            Optional<CartItem> cartItem = getCartItemByProduct(product, cart);

            if (cartItem.isPresent()) {
                validateIfSufficientStock(product, quantity);
                cartItem.get().setQuantity(quantity);
            }

            LOGGER.debug("Update cart. Product id: {}", productId);
            recalculateCart(cart);
        }
    }

    @Override
    public void delete(Cart cart, UUID productId) {
        synchronized (cart) {
            cart.getItems().stream()
                    .filter(item -> productId.equals(item.getProduct().getId()))
                    .findAny()
                    .ifPresent(cart.getItems()::remove);

            LOGGER.debug("Delete product from cart. Product id: {}", productId);
            recalculateCart(cart);
        }
    }

    @Override
    public void clear(HttpSession session) {
        Cart cart = this.get(session);
        String sessionId = session.getId();

        synchronized (sessionId.intern()) {
            cart.getItems().clear();

            LOGGER.debug("Clear cart. Session id: {}", sessionId);
            recalculateCart(cart);
        }
    }

    private void recalculateCart(Cart cart) {
        List<CartItem> items = cart.getItems();

        int totalQuantity = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        BigDecimal totalCost = items.stream()
                .map(item ->
                        item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalQuantity(totalQuantity);
        cart.setTotalCost(totalCost);
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

    private void validateIfNegativeQuantity(int quantity) throws IllegalArgumentException {
        if (quantity <= 0) {
            throw new IllegalArgumentException();
        }
    }

    private void validateIfSufficientStock(Product product, int quantity) throws OutOfStockException {
        if (product.getStock() < quantity) {
            throw new OutOfStockException();
        }
    }

}
