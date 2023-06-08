package com.es.phoneshop.web;

import com.es.phoneshop.model.enums.SearchMethod;
import com.es.phoneshop.service.impl.ProductServiceImpl;
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

import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.ERRORS;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.MAX_PRICE;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.MIN_PRICE;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.PRODUCTS;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.SEARCH_METHOD;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdvancedSearchPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @InjectMocks
    private AdvancedSearchPageServlet servlet = new AdvancedSearchPageServlet();

    @Before
    public void setup() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void init_ShouldCreateValidInstanciesOfClasses() throws NoSuchFieldException, IllegalAccessException, ServletException {
        // given
        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);
        ProductServiceImpl productService = ProductServiceImpl.getInstance();

        // when
        Field productServiceField = AdvancedSearchPageServlet.class.getDeclaredField("productService");
        productServiceField.setAccessible(true);

        // then
        assertEquals(productService.getClass(), productServiceField.get(servlet).getClass());
    }

    @Test
    public void doGet_ShouldCheckValidRequestAttributes() throws ServletException, IOException {
        // given
        when(request.getParameter(SEARCH_METHOD)).thenReturn(SearchMethod.ANY_WORD.name());
        when(request.getParameter(MIN_PRICE)).thenReturn(null);
        when(request.getParameter(MAX_PRICE)).thenReturn(null);

        // when
        servlet.doGet(request, response);

        // then
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq(PRODUCTS), any());
        verify(request, never()).setAttribute(eq(ERRORS), any());
    }

}
