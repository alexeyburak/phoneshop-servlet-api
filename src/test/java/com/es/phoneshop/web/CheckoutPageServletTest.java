package com.es.phoneshop.web;

import com.es.phoneshop.model.Cart;
import com.es.phoneshop.model.Order;
import com.es.phoneshop.model.enums.PaymentMethod;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.OrderService;
import com.es.phoneshop.service.impl.CartServiceImpl;
import com.es.phoneshop.service.impl.OrderServiceImpl;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.UUID;

import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.DELIVERY_ADDRESS;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.DELIVERY_DATE;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.ERRORS;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.FIRST_NAME;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.LAST_NAME;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.ORDER;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.PAYMENT_METHOD;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.PAYMENT_METHODS;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.PHONE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private HttpSession httpSession;
    @Mock
    private CartService cartService;
    @Mock
    private OrderService orderService;
    private String ip;
    @InjectMocks
    private CheckoutPageServlet servlet = new CheckoutPageServlet();

    @Before
    public void setUp() {
        ip = "1.1.1.1";
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getRemoteAddr()).thenReturn(ip);
        when(request.getParameter(PAYMENT_METHOD)).thenReturn(PaymentMethod.CASH.toString());
    }

    @Test
    public void init_ShouldCreateValidInstanciesOfClasses() throws NoSuchFieldException, IllegalAccessException, ServletException {
        // given
        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);
        CartService cartService = CartServiceImpl.getInstance();
        OrderService orderService = OrderServiceImpl.getInstance();

        // when
        Field cartServiceField = CheckoutPageServlet.class.getDeclaredField("cartService");
        cartServiceField.setAccessible(true);
        Field orderServiceField = CheckoutPageServlet.class.getDeclaredField("orderService");
        orderServiceField.setAccessible(true);

        // then
        assertEquals(cartService.getClass(), cartServiceField.get(servlet).getClass());
        assertEquals(orderService.getClass(), orderServiceField.get(servlet).getClass());
    }

    @Test
    public void doGet_ShouldCheckValidRequestAttributes() throws ServletException, IOException {
        // given
        when(cartService.get(any())).thenReturn(new Cart());
        when(orderService.get(any(), any())).thenReturn(new Order());

        // when
        servlet.doGet(request, response);

        // then
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq(PAYMENT_METHODS), any());
        verify(request).setAttribute(eq(ORDER), any());
    }

    @Test
    public void doPost_ValidParameters_ShouldRedirectToSuccessPage() throws ServletException, IOException {
        // given
        final Cart cart = new Cart();
        final UUID id = UUID.randomUUID();
        final Order order = new Order();
        order.setId(id);
        when(cartService.get(httpSession)).thenReturn(cart);
        when(orderService.get(cart, ip)).thenReturn(order);
        when(request.getParameter(FIRST_NAME)).thenReturn("alexey");
        when(request.getParameter(LAST_NAME)).thenReturn("burak");
        when(request.getParameter(PHONE)).thenReturn("375291111112");
        when(request.getParameter(DELIVERY_ADDRESS)).thenReturn("minsk");
        when(request.getParameter(DELIVERY_DATE))
                .thenReturn(LocalDate.now().plusDays(1).toString());

        // when
        servlet.doPost(request, response);

        // then
        verify(response).sendRedirect(request.getContextPath() + "/order/overview/" + id);
        verify(request, never()).setAttribute(eq(ERRORS), any());
    }

    @Test
    public void doPost_InvalidParameters_ShouldAddErrorAttributes() throws ServletException, IOException {
        // given
        final Cart cart = new Cart();
        when(cartService.get(httpSession)).thenReturn(cart);
        when(orderService.get(cart, ip)).thenReturn(new Order());
        when(request.getParameter(FIRST_NAME)).thenReturn(EMPTY);
        when(request.getParameter(LAST_NAME)).thenReturn(EMPTY);
        when(request.getParameter(PHONE)).thenReturn(EMPTY);
        when(request.getParameter(DELIVERY_ADDRESS)).thenReturn(EMPTY);
        when(request.getParameter(DELIVERY_DATE)).thenReturn(LocalDate.now().toString());

        // when
        servlet.doPost(request, response);

        // then
        verify(response, never()).sendRedirect(any());
        verify(request).setAttribute(eq(ERRORS), any());
    }

    @Test
    public void doPost_InvalidPhone_ShouldAddErrorAttributes() throws ServletException, IOException {
        // given
        final Cart cart = new Cart();
        when(cartService.get(httpSession)).thenReturn(cart);
        when(orderService.get(cart, ip)).thenReturn(new Order());
        when(request.getParameter(FIRST_NAME)).thenReturn("alexey");
        when(request.getParameter(LAST_NAME)).thenReturn("burak");
        when(request.getParameter(PHONE)).thenReturn("3752");
        when(request.getParameter(DELIVERY_ADDRESS)).thenReturn("minsk");
        when(request.getParameter(DELIVERY_DATE)).thenReturn(LocalDate.now().toString());

        // when
        servlet.doPost(request, response);

        // then
        verify(response, never()).sendRedirect(any());
        verify(request).setAttribute(eq(ERRORS), any());
    }

    @Test
    public void doPost_InvalidDate_ShouldAddErrorAttributes() throws ServletException, IOException {
        // given
        final Cart cart = new Cart();
        when(cartService.get(httpSession)).thenReturn(cart);
        when(orderService.get(cart, ip)).thenReturn(new Order());
        when(request.getParameter(FIRST_NAME)).thenReturn("alexey");
        when(request.getParameter(LAST_NAME)).thenReturn("burak");
        when(request.getParameter(PHONE)).thenReturn("375298888888");
        when(request.getParameter(DELIVERY_ADDRESS)).thenReturn("minsk");
        when(request.getParameter(DELIVERY_DATE))
                .thenReturn(LocalDate.now().minusDays(1).toString());

        // when
        servlet.doPost(request, response);

        // then
        verify(response, never()).sendRedirect(any());
        verify(request).setAttribute(eq(ERRORS), any());
    }

}
