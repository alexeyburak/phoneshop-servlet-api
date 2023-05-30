package com.es.phoneshop.service;

import com.es.phoneshop.model.Cart;
import com.es.phoneshop.model.Order;
import com.es.phoneshop.model.enums.PaymentMethod;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order get(Cart cart, String ip);
    Order getById(@NonNull UUID id);
    void place(Order order, HttpSession session);
    List<PaymentMethod> getPaymentMethods();
}
