package com.es.phoneshop.web;

import com.es.phoneshop.model.Cart;
import com.es.phoneshop.model.Customer;
import com.es.phoneshop.model.Order;
import com.es.phoneshop.model.enums.PaymentMethod;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.OrderService;
import com.es.phoneshop.service.impl.CartServiceImpl;
import com.es.phoneshop.service.impl.OrderServiceImpl;
import com.es.phoneshop.service.validator.ValidatorFactory;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.es.phoneshop.web.constant.ServletConstant.Message.DATE_IS_NOT_VALID;
import static com.es.phoneshop.web.constant.ServletConstant.Message.EMPTY_VALUE;
import static com.es.phoneshop.web.constant.ServletConstant.Message.PHONE_IS_NOT_VALID;
import static com.es.phoneshop.web.constant.ServletConstant.REQUEST_DISPATCHER_CHECKOUT;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.DELIVERY_ADDRESS;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.DELIVERY_DATE;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.ERRORS;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.FIRST_NAME;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.LAST_NAME;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.ORDER;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.PAYMENT_METHOD;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.PAYMENT_METHODS;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.PHONE;

public class CheckoutPageServlet extends HttpServlet {
    private CartService cartService;
    private OrderService orderService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = CartServiceImpl.getInstance();
        orderService = OrderServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cart cart = cartService.get(request.getSession());

        request.setAttribute(PAYMENT_METHODS, orderService.getPaymentMethods());
        request.setAttribute(ORDER, orderService.get(cart, request.getRemoteAddr()));
        request.getRequestDispatcher(REQUEST_DISPATCHER_CHECKOUT).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cart cart = cartService.get(request.getSession());
        Order order = orderService.get(cart, request.getRemoteAddr());
        Map<String, String> errors = new HashMap<>();

        generateOrder(request, order, errors);

        placeOrderAndHandleError(request, response, order, errors);
    }

    private void generateOrder(HttpServletRequest request, Order order, Map<String, String> errors) {
        Customer customer = new Customer();

        setCustomerParameters(request, errors, customer);
        setPhoneNumber(request, errors, customer);
        setPaymentMethod(request, errors, order);
        setDeliveryDate(request, errors, order);

        order.setCustomer(customer);
    }

    private void setCustomerParameters(HttpServletRequest request, Map<String, String> errors, Customer customer) {
        setRequiredParameter(request, FIRST_NAME, errors, customer::setFirstName);
        setRequiredParameter(request, LAST_NAME, errors, customer::setLastName);
        setRequiredParameter(request, DELIVERY_ADDRESS, errors, customer::setAddress);
    }

    private void setRequiredParameter(HttpServletRequest request, String parameter, Map<String, String> errors,
                                      Consumer<String> consumer) {
        String value = request.getParameter(parameter);
        boolean isNotEmpty = ValidatorFactory.getInstance().getEmptyValidator().isValid(value);

        if (isNotEmpty) {
            consumer.accept(value);
        } else {
            errors.put(parameter, EMPTY_VALUE);
        }
    }

    private void setPhoneNumber(HttpServletRequest request, Map<String, String> errors, Customer customer) {
        String phone = request.getParameter(PHONE);
        boolean isValid = ValidatorFactory.getInstance().getPhoneNumberValidator().isValid(phone);

        if (isValid) {
            customer.setPhone(phone);
        } else {
            errors.put(PHONE, PHONE_IS_NOT_VALID);
        }
    }

    private void setPaymentMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        String method = request.getParameter(PAYMENT_METHOD);
        boolean isValid = ValidatorFactory.getInstance().getPaymentMethodValidator().isValid(method);

        if (isValid) {
            order.setPaymentMethod(PaymentMethod.valueOf(method));
        } else {
            errors.put(PAYMENT_METHOD, EMPTY_VALUE);
        }
    }

    private void setDeliveryDate(HttpServletRequest request, Map<String, String> errors, Order order) {
        String date = request.getParameter(DELIVERY_DATE);
        boolean isValid = ValidatorFactory.getInstance().getDeliveryDateValidator().isValid(date);

        if (isValid) {
            order.setDeliveryDate(LocalDate.parse(date));
        } else {
            errors.put(DELIVERY_DATE, DATE_IS_NOT_VALID);
        }
    }

    private void placeOrderAndHandleError(HttpServletRequest request, HttpServletResponse response,
                                          Order order,
                                          Map<String, String> errors) throws IOException, ServletException {
        if (errors.isEmpty()) {
            orderService.place(order, request.getSession());
            response.sendRedirect(request.getContextPath() + "/order/overview/" + order.getId());
        } else {
            request.setAttribute(ERRORS, errors);
            request.setAttribute(ORDER, order);
            request.setAttribute(PAYMENT_METHODS, orderService.getPaymentMethods());
            request.getRequestDispatcher(REQUEST_DISPATCHER_CHECKOUT).forward(request, response);
        }
    }

}
