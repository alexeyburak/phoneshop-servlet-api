package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.Cart;
import com.es.phoneshop.model.ProductSortCriteria;
import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.Parser;
import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.RecentlyViewedProductsService;
import com.es.phoneshop.service.impl.CartServiceImpl;
import com.es.phoneshop.service.impl.ProductServiceImpl;
import com.es.phoneshop.service.impl.QuantityParserImpl;
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

import static com.es.phoneshop.web.constant.ServletConstant.Message.INVALID_NUMBER_FORMAT;
import static com.es.phoneshop.web.constant.ServletConstant.Message.NEGATIVE_VALUE;
import static com.es.phoneshop.web.constant.ServletConstant.Message.NOT_A_NUMBER;
import static com.es.phoneshop.web.constant.ServletConstant.Message.NOT_IN_STOCK;
import static com.es.phoneshop.web.constant.ServletConstant.REQUEST_DISPATCHER_PRODUCTS;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.ERROR;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.PRODUCTS;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.RECENTLY_VIEWED;
import static com.es.phoneshop.web.constant.ServletConstant.RequestParameter.ORDER;
import static com.es.phoneshop.web.constant.ServletConstant.RequestParameter.PRODUCT_ID;
import static com.es.phoneshop.web.constant.ServletConstant.RequestParameter.QUANTITY;
import static com.es.phoneshop.web.constant.ServletConstant.RequestParameter.QUERY;
import static com.es.phoneshop.web.constant.ServletConstant.RequestParameter.SORT;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class ProductListPageServlet extends HttpServlet {
    private ProductService productService;
    private CartService cartService;
    private RecentlyViewedProductsService recentlyViewedService;
    private Parser<Integer> quantityParser;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ProductServiceImpl.getInstance();
        cartService = CartServiceImpl.getInstance();
        recentlyViewedService = RecentlyViewedProductsServiceImpl.getInstance();
        quantityParser = QuantityParserImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = parseQuery(request);
        ProductSortCriteria sortCriteria = parseSortCriteria(request);

        request.setAttribute(PRODUCTS, productService.findProducts(query, sortCriteria));
        request.setAttribute(RECENTLY_VIEWED, recentlyViewedService.get(request.getSession()).getProducts());
        request.getRequestDispatcher(REQUEST_DISPATCHER_PRODUCTS).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID id = UUID.fromString(request.getParameter(PRODUCT_ID));

        if (isProductAddedToCart(request, response, id)) {
            String location = getLocationForRedirect(request);
            response.sendRedirect(location);
        }
    }

    private static String parseQuery(HttpServletRequest request) {
        return Optional.ofNullable(request.getParameter(QUERY))
                .orElse(EMPTY);
    }

    private ProductSortCriteria parseSortCriteria(HttpServletRequest request) {
        String sortField = request.getParameter(SORT);
        String sortOrder = request.getParameter(ORDER);

        return isValidSortCriteria(sortField, sortOrder) ? ProductSortCriteria.builder()
                .sortField(SortField.valueOf(sortField))
                .sortOrder(SortOrder.valueOf(sortOrder))
                .build() : null;
    }

    private boolean isValidSortCriteria(String sortField, String sortOrder) {
        return sortField != null && sortOrder != null &&
                !sortField.isEmpty() && !sortOrder.isEmpty();
    }

    private boolean isProductAddedToCart(HttpServletRequest request, HttpServletResponse response, UUID id)
            throws ServletException, IOException {
        int quantity;

        try {
            quantity = quantityParser.parse(request.getParameter(QUANTITY), request.getLocale());
        } catch (ParseException e) {
            handleErrorAndForward(request, response, NOT_A_NUMBER);
            return false;
        } catch (NumberFormatException e) {
            handleErrorAndForward(request, response, INVALID_NUMBER_FORMAT);
            return false;
        }

        Cart cart = cartService.get(request.getSession());
        try {
            cartService.add(cart, id, quantity);
        } catch (OutOfStockException e) {
            handleErrorAndForward(request, response, NOT_IN_STOCK);
            return false;
        } catch (IllegalArgumentException e) {
            handleErrorAndForward(request, response, NEGATIVE_VALUE);
            return false;
        }
        return true;
    }

    private void handleErrorAndForward(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute(ERROR, message);
        doGet(request, response);
    }

    private String getLocationForRedirect(HttpServletRequest request) {
        Optional<ProductSortCriteria> sortCriteria = Optional.ofNullable(parseSortCriteria(request));
        String query = parseQuery(request);
        StringBuilder builder = new StringBuilder(request.getContextPath() +
                "/products?message=Product added to cart");

        appendQueryIfPresent(builder, query);
        sortCriteria.ifPresent(criteria ->
                appendSortCriteria(builder, criteria)
        );

        return builder.toString();
    }

    private void appendQueryIfPresent(StringBuilder builder, String query) {
        if (!query.isEmpty()) {
            builder.append("&query=")
                    .append(query);
        }
    }

    private void appendSortCriteria(StringBuilder builder, ProductSortCriteria criteria) {
        builder.append("&sort=")
                .append(criteria.getSortField())
                .append("&order=")
                .append(criteria.getSortOrder());
    }

}
