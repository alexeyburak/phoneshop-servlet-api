package com.es.phoneshop.web;

import com.es.phoneshop.model.ProductSortCriteria;
import com.es.phoneshop.model.RecentlyViewedProductUnit;
import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.RecentlyViewedProductsService;
import com.es.phoneshop.service.impl.ProductServiceImpl;
import com.es.phoneshop.service.impl.RecentlyViewedProductsServiceImpl;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ProductService productService;
    @Mock
    private RecentlyViewedProductsService recentlyViewedProductsService;
    @InjectMocks
    private ProductListPageServlet servlet = new ProductListPageServlet();

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
        RecentlyViewedProductsServiceImpl recentlyViewedService = RecentlyViewedProductsServiceImpl.getInstance();

        // when
        Field productServiceField = ProductListPageServlet.class.getDeclaredField("productService");
        productServiceField.setAccessible(true);
        Field recentlyViewedServiceField = ProductListPageServlet.class.getDeclaredField("recentlyViewedService");
        recentlyViewedServiceField.setAccessible(true);

        // then
        assertEquals(productService.getClass(), productServiceField.get(servlet).getClass());
        assertEquals(recentlyViewedService.getClass(), recentlyViewedServiceField.get(servlet).getClass());
    }

    @Test
    public void doGet_ShouldCheckValidRequestAttributes() throws ServletException, IOException {
        // given
        when(recentlyViewedProductsService.get(any())).thenReturn(new RecentlyViewedProductUnit());

        // when
        servlet.doGet(request, response);

        // then
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("products"), any());
        verify(request).setAttribute(eq("recentlyViewed"), any());
    }

    @Test
    public void getSortCriteria_ValidParameters_ShouldReturnProductSortCriteriaObject() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        when(request.getParameter("sort")).thenReturn("price");
        when(request.getParameter("order")).thenReturn("asc");
        Method getSortCriteriaMethod = ProductListPageServlet.class.getDeclaredMethod("getSortCriteria", HttpServletRequest.class);
        getSortCriteriaMethod.setAccessible(true);

        // when
        ProductSortCriteria criteria = (ProductSortCriteria) getSortCriteriaMethod.invoke(servlet, request);

        // then
        assertEquals(SortField.price, criteria.getSortField());
        assertEquals(SortOrder.asc, criteria.getSortOrder());
    }

    @Test
    public void getSortCriteria_NullParameters_ShouldReturnNull() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        when(request.getParameter("sort")).thenReturn(null);
        when(request.getParameter("order")).thenReturn(null);
        Method getSortCriteriaMethod = ProductListPageServlet.class.getDeclaredMethod("getSortCriteria", HttpServletRequest.class);
        getSortCriteriaMethod.setAccessible(true);

        // when
        ProductSortCriteria criteria = (ProductSortCriteria) getSortCriteriaMethod.invoke(servlet, request);

        // then
        assertNull(criteria);
    }
}