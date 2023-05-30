package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.model.Customer;
import com.es.phoneshop.model.Order;
import com.es.phoneshop.model.enums.PaymentMethod;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OrderDaoImplTest {
    private OrderDao orderDao;
    private List<Order> orders;
    private Order order;

    @Before
    public void setup() {
        order = new Order(UUID.randomUUID(), null, 1, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, new Customer(), LocalDate.now(),
                PaymentMethod.CASH);
        orders = new ArrayList<>(
                List.of(order)
        );

        orderDao = createOrderDaoWithProducts(orders);
    }

    @SneakyThrows
    private OrderDaoImpl createOrderDaoWithProducts(final List<Order> orders) {
        OrderDaoImpl orderDao;

        Constructor<OrderDaoImpl> constructor = OrderDaoImpl.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        orderDao = constructor.newInstance();

        Field ordersField = AbstractGenericDao.class.getDeclaredField("items");
        ordersField.setAccessible(true);
        ordersField.set(orderDao, orders);

        return orderDao;
    }

    @Test
    public void getInstance_ShouldCreateAndReturnOneSameInstance() {
        // given
        OrderDaoImpl instance1;
        OrderDaoImpl instance2;

        // when
        instance1 = OrderDaoImpl.getInstance();
        instance2 = OrderDaoImpl.getInstance();

        // then
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertEquals(instance1, instance2);
    }

    @Test
    public void getOrder_ValidId_ShouldReturnOrderFromDao() {
        // given
        final UUID id = order.getId();

        // when
        Order result = orderDao.get(id).get();

        // then
        assertNotNull(result);
        assertEquals(order, result);
    }

    @Test(expected = NullPointerException.class)
    public void save_NullOrder_ShouldThrowNullPointerException() {
        // given
        final Order order = null;

        // when
        orderDao.save(order);

        // then (exception is thrown)
    }

    @Test
    public void save_NotNullOrder_ShouldAddOrderToDao() {
        // given
        final BigDecimal bigDecimalTen = BigDecimal.TEN;
        final Order orderToAdd = new Order(UUID.randomUUID(), null, 1, bigDecimalTen,
                bigDecimalTen, bigDecimalTen, new Customer(), LocalDate.now(),
                PaymentMethod.CASH);
        final int expectedSize = orders.size() + 1;

        // when
        orderDao.save(orderToAdd);

        // then
        assertTrue(orders.contains(orderToAdd));
        assertEquals(expectedSize, orders.size());

    }
}
