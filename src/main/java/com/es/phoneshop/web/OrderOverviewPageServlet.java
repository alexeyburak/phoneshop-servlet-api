package com.es.phoneshop.web;

import com.es.phoneshop.service.OrderService;
import com.es.phoneshop.service.impl.OrderServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

import static com.es.phoneshop.web.constant.ServletConstant.REQUEST_DISPATCHER_OVERVIEW;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.ORDER;

public class OrderOverviewPageServlet extends HttpServlet {
    private OrderService orderService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderService = OrderServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID id = parseOrderId(request);

        request.setAttribute(ORDER, orderService.getById(id));
        request.getRequestDispatcher(REQUEST_DISPATCHER_OVERVIEW).forward(request, response);
    }

    private UUID parseOrderId(HttpServletRequest request) {
        return UUID.fromString(request.getPathInfo().substring(1));
    }

}
