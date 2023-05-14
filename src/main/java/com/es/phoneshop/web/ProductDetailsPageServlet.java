package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.Cart;
import com.es.phoneshop.model.RecentlyViewedProductUnit;
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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.UUID;

public class ProductDetailsPageServlet extends HttpServlet {
    private static final String REQUEST_ATTRIBUTE_PRODUCT = "product";
    private static final String REQUEST_ATTRIBUTE_CART = "cart";
    private static final String REQUEST_ATTRIBUTE_ERROR = "error";
    private static final String REQUEST_ATTRIBUTE_RECENTLY_VIEWED = "recentlyViewed";
    private static final String REQUEST_PARAMETER_QUANTITY = "quantity";
    private static final String REQUEST_DISPATCHER_PRODUCT = "/WEB-INF/pages/product.jsp";
    private static final String MESSAGE_NOT_A_NUMBER = "Not a number";
    private static final String MESSAGE_NOT_IN_STOCK = "Not available in stock";
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
        UUID id = parseProductId(request);
        HttpSession session = request.getSession();
        RecentlyViewedProductUnit productUnit = recentlyViewedService.get(session);

        recentlyViewedService.add(id, productUnit);

        request.setAttribute(REQUEST_ATTRIBUTE_PRODUCT, productService.getProduct(id));
        request.setAttribute(REQUEST_ATTRIBUTE_CART, cartService.get(session));
        request.setAttribute(REQUEST_ATTRIBUTE_RECENTLY_VIEWED, productUnit.getProducts());
        request.getRequestDispatcher(REQUEST_DISPATCHER_PRODUCT).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID id = parseProductId(request);

        if (isProductAddedToCart(request, response, id)) {
            response.sendRedirect(request.getContextPath() + "/products/" + id + "?message=Product added to cart");
        }
    }

    private UUID parseProductId(HttpServletRequest request) {
        return UUID.fromString(request.getPathInfo().substring(1));
    }

    private boolean isProductAddedToCart(HttpServletRequest request, HttpServletResponse response, UUID id)
            throws ServletException, IOException {
        int quantity;

        try {
            quantity = parseQuantity(request);
        } catch (ParseException e) {
            handleErrorAndForward(request, response, MESSAGE_NOT_A_NUMBER);
            return false;
        }

        Cart cart = cartService.get(request.getSession());
        try {
            cartService.add(cart, id, quantity);
        } catch (OutOfStockException e) {
            handleErrorAndForward(request, response, MESSAGE_NOT_IN_STOCK);
            return false;
        }
        return true;
    }

    private int parseQuantity(HttpServletRequest request) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(request.getLocale());
        return format.parse(request.getParameter(REQUEST_PARAMETER_QUANTITY)).intValue();
    }

    private void handleErrorAndForward(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute(REQUEST_ATTRIBUTE_ERROR, message);
        doGet(request, response);
    }

}
