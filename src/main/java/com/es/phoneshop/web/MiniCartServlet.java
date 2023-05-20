package com.es.phoneshop.web;

import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.CartServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class MiniCartServlet extends HttpServlet {
    private static final String REQUEST_DISPATCHER_MINI_CART = "/WEB-INF/pages/minicart.jsp";
    private static final String REQUEST_ATTRIBUTE_CART = "cart";
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = CartServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(REQUEST_ATTRIBUTE_CART, cartService.get(request.getSession()));
        request.getRequestDispatcher(REQUEST_DISPATCHER_MINI_CART).include(request, response);
    }

}
