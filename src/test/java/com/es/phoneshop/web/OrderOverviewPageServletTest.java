package com.es.phoneshop.web;

import com.es.phoneshop.service.OrderService;
import com.es.phoneshop.service.impl.OrderServiceImpl;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.ORDER;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderOverviewPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private OrderService orderService;
    @InjectMocks
    private OrderOverviewPageServlet servlet = new OrderOverviewPageServlet();

    @Before
    public void setUp() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void init_ShouldCreateValidInstanciesOfClasses() throws NoSuchFieldException, IllegalAccessException, ServletException {
        // given
        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);
        OrderService orderService = OrderServiceImpl.getInstance();

        // when
        Field orderServiceField = OrderOverviewPageServlet.class.getDeclaredField("orderService");
        orderServiceField.setAccessible(true);

        // then
        assertEquals(orderService.getClass(), orderServiceField.get(servlet).getClass());
    }

    @Test
    public void doGet_ShouldCheckValidRequestAttributes() throws ServletException, IOException {
        // given
        when(request.getPathInfo()).thenReturn(UUID.randomUUID().toString());

        // when
        servlet.doGet(request, response);

        // then
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq(ORDER), any());
    }

}
