package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.CartServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class CartPageServlet extends HttpServlet {
    private static final String REQUEST_ATTRIBUTE_CART = "cart";
    private static final String REQUEST_ATTRIBUTE_ERRORS = "errors";
    private static final String REQUEST_PARAMETER_QUANTITY = "quantity";
    private static final String REQUEST_PARAMETER_PRODUCT_ID = "productId";
    private static final String REQUEST_DISPATCHER_CART = "/WEB-INF/pages/cart.jsp";
    private static final String MESSAGE_NOT_A_NUMBER = "Not a number";
    private static final String MESSAGE_NOT_IN_STOCK = "Not available in stock";
    private static final String MESSAGE_NEGATIVE_VALUE = "Negative value";
    private static final String MESSAGE_INVALID_NUMBER_FORMAT = "Invalid number format";
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = CartServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cart cart = cartService.get(request.getSession());

        request.setAttribute(REQUEST_ATTRIBUTE_CART, cart);
        request.getRequestDispatcher(REQUEST_DISPATCHER_CART).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> parameters = getParametersAsMap(request);
        Map<UUID, String> errors = new HashMap<>();

        updateCartItem(request, errors, parameters);

        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
        } else {
            request.setAttribute(REQUEST_ATTRIBUTE_ERRORS, errors);
            doGet(request, response);
        }
    }

    private Map<String, String> getParametersAsMap(HttpServletRequest request) {
        String[] productIds = request.getParameterValues(REQUEST_PARAMETER_PRODUCT_ID);
        String[] quantities = request.getParameterValues(REQUEST_PARAMETER_QUANTITY);
        Map<String, String> parameters = new HashMap<>();

        IntStream.range(0, productIds.length)
                .forEach(index ->
                        parameters.put(productIds[index], quantities[index])
                );

        return parameters;
    }

    private void updateCartItem(HttpServletRequest request, Map<UUID, String> errors,
                                Map<String, String> parameters) {
        Cart cart = cartService.get(request.getSession());
        Locale locale = request.getLocale();

        parameters.forEach((productId, quantityString) -> {
                    UUID id = UUID.fromString(productId);
                    try {
                        int quantity = parseQuantity(quantityString, locale);
                        cartService.update(cart, id, quantity);
                    } catch (ParseException e) {
                        handleError(errors, id, MESSAGE_NOT_A_NUMBER);
                    } catch (OutOfStockException e) {
                        handleError(errors, id, MESSAGE_NOT_IN_STOCK);
                    } catch (NumberFormatException e) {
                        handleError(errors, id, MESSAGE_INVALID_NUMBER_FORMAT);
                    } catch (IllegalArgumentException e) {
                        handleError(errors, id, MESSAGE_NEGATIVE_VALUE);
                    }
                }
        );
    }

    private int parseQuantity(String quantity, Locale locale) throws ParseException, NumberFormatException {
        NumberFormat format = NumberFormat.getInstance(locale);

        validateInteger(quantity);
        return format.parse(quantity).intValue();
    }

    private void validateInteger(String value) {
        String DIGIT_REGEX = "^\\d+$";
        if (!Pattern.matches(DIGIT_REGEX, value)) {
            throw new NumberFormatException();
        }
    }

    private void handleError(Map<UUID, String> errors, UUID productId, String message) {
        errors.put(productId, message);
    }

}
