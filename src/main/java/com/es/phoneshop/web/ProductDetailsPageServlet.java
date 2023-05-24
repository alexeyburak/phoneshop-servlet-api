package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.Cart;
import com.es.phoneshop.model.RecentlyViewedProductUnit;
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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.es.phoneshop.web.constant.ServletConstant.Message.INVALID_NUMBER_FORMAT;
import static com.es.phoneshop.web.constant.ServletConstant.Message.NEGATIVE_VALUE;
import static com.es.phoneshop.web.constant.ServletConstant.Message.NOT_A_NUMBER;
import static com.es.phoneshop.web.constant.ServletConstant.Message.NOT_IN_STOCK;
import static com.es.phoneshop.web.constant.ServletConstant.REQUEST_DISPATCHER_PRODUCT;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.CART;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.ERROR;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.PRODUCT;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.RECENTLY_VIEWED;
import static com.es.phoneshop.web.constant.ServletConstant.RequestParameter.QUANTITY;

public class ProductDetailsPageServlet extends HttpServlet {
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
        UUID id = parseProductId(request);
        HttpSession session = request.getSession();
        RecentlyViewedProductUnit productUnit = recentlyViewedService.get(session);

        recentlyViewedService.add(id, productUnit);

        request.setAttribute(PRODUCT, productService.getProduct(id));
        request.setAttribute(CART, cartService.get(session));
        request.setAttribute(RECENTLY_VIEWED, productUnit.getProducts());
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

}
