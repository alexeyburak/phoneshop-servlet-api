package com.es.phoneshop.web;

import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.implementation.ProductServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

public class ProductDetailsPageServlet extends HttpServlet {
    private static final String REQUEST_ATTRIBUTE_PRODUCT = "product";
    private static final String REQUEST_DISPATCHER_PRODUCT = "/WEB-INF/pages/product.jsp";
    private ProductService productService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = new ProductServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID id = UUID.fromString(request.getPathInfo().substring(1));

        request.setAttribute(REQUEST_ATTRIBUTE_PRODUCT, productService.getProduct(id));
        request.getRequestDispatcher(REQUEST_DISPATCHER_PRODUCT).forward(request, response);
    }

}
