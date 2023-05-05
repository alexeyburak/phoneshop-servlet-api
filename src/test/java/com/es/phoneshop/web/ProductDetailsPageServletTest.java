package com.es.phoneshop.web;

import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.service.ProductService;
import jakarta.servlet.RequestDispatcher;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ProductService productService;
    @InjectMocks
    private ProductDetailsPageServlet servlet;

    @Before
    public void setup() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void doGet_ValidProductId_ShouldCheckRequestAttribute() throws ServletException, IOException {
        // given
        when(request.getPathInfo()).thenReturn(UUID.randomUUID().toString());

        // when
        servlet.doGet(request, response);

        // then
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("product"), any());
    }

    @Test(expected = ProductNotFoundException.class)
    public void doGet_InvalidProductId_ShouldTrowProductNotFoundException() throws ServletException, IOException {
        // given
        final UUID invalidId = UUID.randomUUID();
        when(request.getPathInfo()).thenReturn("/" + invalidId);
        when(productService.getProduct(invalidId)).thenThrow(new ProductNotFoundException());

        // when
        servlet.doGet(request, response);

        // then (exception is thrown)
    }

}
