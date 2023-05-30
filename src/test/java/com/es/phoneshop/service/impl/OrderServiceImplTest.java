package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.exception.ApiRequestException;
import com.es.phoneshop.exception.OrderNotFoundException;
import com.es.phoneshop.model.Cart;
import com.es.phoneshop.model.Order;
import com.es.phoneshop.model.enums.PaymentMethod;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.DistanceMatrixService;
import com.es.phoneshop.service.IPAddressGeolocationService;
import com.es.phoneshop.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {
    private static final BigDecimal DEFAULT_DELIVERY_COST = BigDecimal.TEN;
    @Mock
    private OrderDao orderDao;
    @Mock
    private CartService cartService;
    @Mock
    private IPAddressGeolocationService ipAddressGeolocationService;
    @Mock
    private DistanceMatrixService distanceMatrixService;
    @Mock
    private HttpSession session;
    private String ip;
    private String destination;
    private OrderService orderService;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        ip = "1.1.1.1";
        destination = "minsk";
        orderService = OrderServiceImpl.getInstance();
        declareFinalFields();
    }

    private void declareFinalFields() throws NoSuchFieldException, IllegalAccessException {
        Field orderDaoField = orderService.getClass().getDeclaredField("orderDao");
        orderDaoField.setAccessible(true);
        orderDaoField.set(orderService, orderDao);

        Field cartServiceField = orderService.getClass().getDeclaredField("cartService");
        cartServiceField.setAccessible(true);
        cartServiceField.set(orderService, cartService);

        Field ipGeolocationServiceField = orderService.getClass().getDeclaredField("ipAddressGeolocationService");
        ipGeolocationServiceField.setAccessible(true);
        ipGeolocationServiceField.set(orderService, ipAddressGeolocationService);

        Field distanceMatrixServiceField = orderService.getClass().getDeclaredField("distanceMatrixService");
        distanceMatrixServiceField.setAccessible(true);
        distanceMatrixServiceField.set(orderService, distanceMatrixService);
    }

    @Test
    public void get_ValidApiRequest_ShouldReturnOrderWithReceivedData() throws ApiRequestException {
        // given
        Cart cart = new Cart();
        cart.setTotalQuantity(5);
        cart.setTotalCost(BigDecimal.valueOf(70));
        final BigDecimal expectedDeliveryCost = BigDecimal.valueOf(50.0);
        final BigDecimal expectedSubTotal = cart.getTotalCost();
        final BigDecimal expectedTotalCost = expectedSubTotal.add(expectedDeliveryCost);
        when(ipAddressGeolocationService.getIpAddressGeolocation(ip)).thenReturn(destination);
        when(distanceMatrixService.getDistanceMatrix(destination)).thenReturn(100);

        // when
        Order order = orderService.get(cart, ip);

        // then
        assertEquals(cart.getTotalQuantity(), order.getTotalQuantity());
        assertEquals(expectedSubTotal, order.getSubTotal());
        assertEquals(expectedDeliveryCost, order.getDeliveryCost());
        assertEquals(expectedTotalCost, order.getTotalCost());
        verify(ipAddressGeolocationService, times(1)).getIpAddressGeolocation(ip);
        verify(distanceMatrixService, times(1)).getDistanceMatrix(destination);
    }

    @Test
    public void get_InvalidIPGeolocationRequest_ShouldReturnOrderWithDefaultData() throws ApiRequestException {
        // given
        Cart cart = new Cart();
        cart.setTotalCost(BigDecimal.valueOf(70));
        final BigDecimal expectedDeliveryCost = DEFAULT_DELIVERY_COST;
        final BigDecimal expectedSubTotal = BigDecimal.valueOf(70);
        final BigDecimal expectedTotalCost = expectedSubTotal.add(expectedDeliveryCost);
        when(ipAddressGeolocationService.getIpAddressGeolocation(ip)).thenThrow(ApiRequestException.class);

        // when
        Order order = orderService.get(cart, ip);

        // then
        assertEquals(expectedSubTotal, order.getSubTotal());
        assertEquals(expectedDeliveryCost, order.getDeliveryCost());
        assertEquals(expectedTotalCost, order.getTotalCost());
        verify(ipAddressGeolocationService, times(1)).getIpAddressGeolocation(ip);
    }

    @Test
    public void get_InvalidDistanceMatrixRequest_ShouldReturnOrderWithDefaultData() throws ApiRequestException {
        // given
        Cart cart = new Cart();
        cart.setTotalQuantity(5);
        cart.setTotalCost(BigDecimal.valueOf(70));
        final BigDecimal expectedDeliveryCost = DEFAULT_DELIVERY_COST;
        final BigDecimal expectedSubTotal = cart.getTotalCost();
        final BigDecimal expectedTotalCost = expectedSubTotal.add(expectedDeliveryCost);
        when(ipAddressGeolocationService.getIpAddressGeolocation(ip)).thenReturn(destination);
        when(distanceMatrixService.getDistanceMatrix(destination)).thenThrow(ApiRequestException.class);

        // when
        Order order = orderService.get(cart, ip);

        // then
        assertEquals(expectedSubTotal, order.getSubTotal());
        assertEquals(expectedDeliveryCost, order.getDeliveryCost());
        assertEquals(expectedTotalCost, order.getTotalCost());
        verify(ipAddressGeolocationService, times(1)).getIpAddressGeolocation(ip);
    }

    @Test
    public void getById_ValidId_ShouldCallOrderDaoMethod() {
        // given
        final UUID id = UUID.randomUUID();
        when(orderDao.get(id)).thenReturn(Optional.of(new Order()));

        // when
        orderService.getById(id);

        // then
        verify(orderDao, times(1)).get(id);
    }

    @Test(expected = NullPointerException.class)
    public void getById_NullId_ShouldThrowNullPointerException() {
        // given
        final UUID id = null;

        // when
        orderService.getById(id);

        // then (exception is thrown)
    }

    @Test(expected = OrderNotFoundException.class)
    public void getById_MissingId_ShouldThrowOrderNotFoundException() {
        // given
        final UUID id = UUID.randomUUID();

        // when
        orderService.getById(id);

        // then (exception is thrown)
    }

    @Test
    public void place_ValidOrder_ShouldCallOrderDaoAndCartServiceMethods() {
        // given
        final Order order = new Order();

        // when
        orderService.place(order, session);

        // then
        assertNotNull(order.getId());
        verify(orderDao, times(1)).save(order);
        verify(cartService, times(1)).clear(session);
    }

    @Test
    public void getPaymentMethods_ShouldReturnListOfPaymentMethods() {
        // given
        final PaymentMethod cash = PaymentMethod.CASH;
        final PaymentMethod card = PaymentMethod.CREDIT_CARD;

        // when
        List<PaymentMethod> result = orderService.getPaymentMethods();

        // then
        assertNotNull(result);
        assertTrue(result.contains(card));
        assertTrue(result.contains(cash));
    }

}
