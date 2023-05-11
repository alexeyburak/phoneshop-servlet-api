package com.es.phoneshop.web;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.Product;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DemoDataServletContextListenerTest {
    @Mock
    private ServletContextEvent servletContextEvent;
    @Mock
    private ServletContext servletContext;
    @Mock
    private ProductDao productDao;
    @InjectMocks
    private DemoDataServletContextListener listener;

    @Before
    public void setup() {
        when(servletContextEvent.getServletContext()).thenReturn(servletContext);
    }

    @Test
    public void contextInitialized_withInsertDemoDataTrue() {
        when(servletContext.getInitParameter("insertDemoData")).thenReturn("true");

        listener.contextInitialized(servletContextEvent);

        verify(productDao, times(12)).save(any(Product.class));
    }

    @Test
    public void contextInitialized_withInsertDemoDataFalse() {
        when(servletContext.getInitParameter("insertDemoData")).thenReturn("false");

        listener.contextInitialized(servletContextEvent);

        verify(productDao, never()).save(any(Product.class));
    }

}
