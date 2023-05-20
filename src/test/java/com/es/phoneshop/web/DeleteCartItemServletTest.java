package com.es.phoneshop.web;

import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.CartServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCartItemServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession httpSession;
    @Mock
    private CartService cartService;
    @InjectMocks
    private DeleteCartItemServlet servlet = new DeleteCartItemServlet();

    @Test
    public void init_ShouldCreateValidInstanciesOfClasses() throws NoSuchFieldException, IllegalAccessException, ServletException {
        // given
        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);
        CartService cartService = CartServiceImpl.getInstance();

        // when
        Field cartServiceField = DeleteCartItemServlet.class.getDeclaredField("cartService");
        cartServiceField.setAccessible(true);

        // then
        assertEquals(cartService.getClass(), cartServiceField.get(servlet).getClass());
    }

    @Test
    public void doPost_ValidPath_ShouldRedirectToSuccessPage() throws IOException {
        // given
        final UUID id = UUID.randomUUID();
        when(request.getPathInfo()).thenReturn(id.toString());
        when(request.getSession()).thenReturn(httpSession);

        // when
        servlet.doPost(request, response);

        // then
        verify(response).sendRedirect(request.getContextPath() + "/cart?message=Item removed successfully");
    }

}
