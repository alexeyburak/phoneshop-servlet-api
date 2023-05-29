package com.es.phoneshop.web;

import com.es.phoneshop.model.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.CartServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

public class DeleteCartItemServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = CartServiceImpl.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UUID id = parseProductId(request);

        Cart cart = cartService.get(request.getSession());
        cartService.delete(cart, id);

        response.sendRedirect(request.getContextPath() + "/cart?message=Item removed successfully");
    }

    private UUID parseProductId(HttpServletRequest request) {
        return UUID.fromString(request.getPathInfo().substring(1));
    }

}
