package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.dao.impl.OrderDaoImpl;
import com.es.phoneshop.exception.ApiRequestException;
import com.es.phoneshop.exception.OrderNotFoundException;
import com.es.phoneshop.model.Cart;
import com.es.phoneshop.model.CartItem;
import com.es.phoneshop.model.Order;
import com.es.phoneshop.model.enums.PaymentMethod;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.DistanceMatrixService;
import com.es.phoneshop.service.IPAddressGeolocationService;
import com.es.phoneshop.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final BigDecimal DEFAULT_DELIVERY_COST = BigDecimal.TEN;
    private final OrderDao orderDao;
    private final CartService cartService;
    private final IPAddressGeolocationService ipAddressGeolocationService;
    private final DistanceMatrixService distanceMatrixService;

    private OrderServiceImpl() {
        orderDao = OrderDaoImpl.getInstance();
        cartService = CartServiceImpl.getInstance();
        ipAddressGeolocationService = IPAddressGeolocationServiceImpl.getInstance();
        distanceMatrixService = DistanceMatrixServiceImpl.getInstance();
    }

    private static final class SingletonHolder {
        private static final OrderServiceImpl INSTANCE = new OrderServiceImpl();
    }

    public static OrderServiceImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public Order get(Cart cart, String ip) {
        Order order = new Order();

        order.setItems(SerializationUtils.clone((ArrayList<CartItem>) cart.getItems()));
        order.setTotalQuantity(cart.getTotalQuantity());
        order.setSubTotal(cart.getTotalCost());
        order.setDeliveryCost(calculateDeliveryCost(ip));
        order.setTotalCost(calculateTotalCost(order));

        LOGGER.debug("Get order. IP address: {}", ip);
        return order;
    }

    @Override
    public Order getById(@NonNull UUID id) {
        LOGGER.debug("Get order by id. Id: {}", id);

        return orderDao.get(id)
                .orElseThrow(OrderNotFoundException::new);
    }

    @Override
    public void place(Order order, HttpSession session) {
        UUID id = UUID.randomUUID();
        order.setId(id);

        orderDao.save(order);
        cartService.clear(session);
        LOGGER.debug("Place order. Order id: {}", id);
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return Arrays.asList(PaymentMethod.values());
    }

    private BigDecimal calculateDeliveryCost(String ip) {
        BigDecimal matrixDistance;
        try {
            String city = ipAddressGeolocationService.getIpAddressGeolocation(ip);
            matrixDistance = getDistanceMatrix(city);
        } catch (ApiRequestException e) {
            return DEFAULT_DELIVERY_COST;
        }

        return matrixDistance.multiply(BigDecimal.valueOf(0.5));
    }

    private BigDecimal getDistanceMatrix(String city) throws ApiRequestException {
        int distance = distanceMatrixService.getDistanceMatrix(city);
        return new BigDecimal(distance);
    }

    private BigDecimal calculateTotalCost(Order order) {
        return order.getSubTotal().add(order.getDeliveryCost());
    }

}
