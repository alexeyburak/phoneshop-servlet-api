package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.Cart;
import com.es.phoneshop.model.ProductSortCriteria;
import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.RecentlyViewedProductsService;
import com.es.phoneshop.service.impl.CartServiceImpl;
import com.es.phoneshop.service.impl.ProductServiceImpl;
import com.es.phoneshop.service.impl.RecentlyViewedProductsServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class ProductListPageServlet extends HttpServlet {
    private static final String REQUEST_ATTRIBUTE_PRODUCTS = "products";
    private static final String REQUEST_PARAMETER_QUANTITY = "quantity";
    private static final String REQUEST_PARAMETER_PRODUCT_ID = "productId";
    private static final String REQUEST_ATTRIBUTE_RECENTLY_VIEWED = "recentlyViewed";
    private static final String REQUEST_DISPATCHER_PRODUCTS = "/WEB-INF/pages/productList.jsp";
    private static final String REQUEST_PARAM_QUERY = "query";
    private static final String REQUEST_PARAM_SORT = "sort";
    private static final String REQUEST_ATTRIBUTE_ERROR = "error";
    private static final String REQUEST_PARAM_ORDER = "order";
    private static final String MESSAGE_NOT_A_NUMBER = "Not a number";
    private static final String MESSAGE_INVALID_NUMBER_FORMAT = "Invalid number format";
    private static final String MESSAGE_NOT_IN_STOCK = "Not available in stock";
    private static final String MESSAGE_NEGATIVE_VALUE = "Negative value";
    private ProductService productService;
    private CartService cartService;
    private RecentlyViewedProductsService recentlyViewedService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ProductServiceImpl.getInstance();
        cartService = CartServiceImpl.getInstance();
        recentlyViewedService = RecentlyViewedProductsServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = Optional.ofNullable(request.getParameter(REQUEST_PARAM_QUERY))
                .orElse(EMPTY);
        ProductSortCriteria sortCriteria = getSortCriteria(request);

        request.setAttribute(REQUEST_ATTRIBUTE_PRODUCTS, productService.findProducts(query, sortCriteria));
        request.setAttribute(REQUEST_ATTRIBUTE_RECENTLY_VIEWED, recentlyViewedService.get(request.getSession()).getProducts());
        request.getRequestDispatcher(REQUEST_DISPATCHER_PRODUCTS).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID id = UUID.fromString(request.getParameter(REQUEST_PARAMETER_PRODUCT_ID));

        if (isProductAddedToCart(request, response, id)) {
            response.sendRedirect(request.getContextPath() + "/products?message=Product added to cart");
        }
    }

    private ProductSortCriteria getSortCriteria(HttpServletRequest request) {
        String sortField = request.getParameter(REQUEST_PARAM_SORT);
        String sortOrder = request.getParameter(REQUEST_PARAM_ORDER);

        return sortField != null && sortOrder != null ? ProductSortCriteria.builder()
                .sortField(SortField.valueOf(sortField))
                .sortOrder(SortOrder.valueOf(sortOrder))
                .build() : null;
    }

    private boolean isProductAddedToCart(HttpServletRequest request, HttpServletResponse response, UUID id)
            throws ServletException, IOException {
        int quantity;

        try {
            quantity = parseQuantity(request);
        } catch (ParseException e) {
            handleErrorAndForward(request, response, MESSAGE_NOT_A_NUMBER);
            return false;
        } catch (NumberFormatException e) {
            handleErrorAndForward(request, response, MESSAGE_INVALID_NUMBER_FORMAT);
            return false;
        }

        Cart cart = cartService.get(request.getSession());
        try {
            cartService.add(cart, id, quantity);
        } catch (OutOfStockException e) {
            handleErrorAndForward(request, response, MESSAGE_NOT_IN_STOCK);
            return false;
        } catch (IllegalArgumentException e) {
            handleErrorAndForward(request, response, MESSAGE_NEGATIVE_VALUE);
            return false;
        }
        return true;
    }

    private int parseQuantity(HttpServletRequest request) throws ParseException, NumberFormatException {
        NumberFormat format = NumberFormat.getInstance(request.getLocale());
        String quantity = request.getParameter(REQUEST_PARAMETER_QUANTITY);

        validateInteger(quantity);
        return format.parse(quantity).intValue();
    }

    private void validateInteger(String value) {
        String DIGIT_REGEX = "^\\d+$";
        if (!Pattern.matches(DIGIT_REGEX, value)) {
            throw new NumberFormatException();
        }
    }

    private void handleErrorAndForward(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute(REQUEST_ATTRIBUTE_ERROR, message);
        doGet(request, response);
    }

}
