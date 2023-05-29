package com.es.phoneshop.web;

import com.es.phoneshop.model.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.CartServiceImpl;
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
import java.util.Locale;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest {
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
    @InjectMocks
    private CartPageServlet servlet = new CartPageServlet();

    @Before
    public void setUp() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getSession()).thenReturn(httpSession);
    }

    @Test
    public void init_ShouldCreateValidInstanciesOfClasses() throws NoSuchFieldException, IllegalAccessException, ServletException {
        // given
        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);
        CartService cartService = CartServiceImpl.getInstance();

        // when
        Field cartServiceField = CartPageServlet.class.getDeclaredField("cartService");
        cartServiceField.setAccessible(true);

        // then
        assertEquals(cartService.getClass(), cartServiceField.get(servlet).getClass());
    }

    @Test
    public void doGet_ShouldCheckValidRequestAttributes() throws ServletException, IOException {
        // given
        when(cartService.get(any())).thenReturn(new Cart());

        // when
        servlet.doGet(request, response);

        // then
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("cart"), any());
    }

    @Test
    public void doPost_ValidParameters_ShouldRedirectToSuccessPage() throws ServletException, IOException {
        // given
        final Cart cart = new Cart();
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        when(request.getParameterValues("quantity")).thenReturn(new String[]{"2", "3"});
        when(request.getParameterValues("productId")).thenReturn(new String[]{id1.toString(), id2.toString()});
        when(cartService.get(httpSession)).thenReturn(cart);

        // when
        servlet.doPost(request, response);

        // then
        verify(response).sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
    }

    @Test
    public void doPost_NotANumberParameter_ShouldRedirectToErrorPage() throws ServletException, IOException {
        // given
        final Cart cart = new Cart();
        UUID id = UUID.randomUUID();
        when(request.getParameterValues("quantity")).thenReturn(new String[]{"a"});
        when(request.getParameterValues("productId")).thenReturn(new String[]{id.toString()});
        when(cartService.get(httpSession)).thenReturn(cart);

        // when
        servlet.doPost(request, response);

        // then
        verify(requestDispatcher).forward(request, response);
        verify(response, never()).sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
    }

    @Test
    public void doPost_NegativeParameter_ShouldRedirectToErrorPage() throws ServletException, IOException {
        // given
        final Cart cart = new Cart();
        UUID id = UUID.randomUUID();
        when(request.getParameterValues("quantity")).thenReturn(new String[]{"-333"});
        when(request.getParameterValues("productId")).thenReturn(new String[]{id.toString()});
        when(cartService.get(httpSession)).thenReturn(cart);

        // when
        servlet.doPost(request, response);

        // then
        verify(requestDispatcher).forward(request, response);
        verify(response, never()).sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
    }

}
