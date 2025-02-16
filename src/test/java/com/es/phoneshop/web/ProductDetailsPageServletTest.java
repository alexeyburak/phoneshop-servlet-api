package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.Cart;
import com.es.phoneshop.model.RecentlyViewedProductUnit;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.RecentlyViewedProductsService;
import com.es.phoneshop.service.impl.CartServiceImpl;
import com.es.phoneshop.service.impl.ProductServiceImpl;
import com.es.phoneshop.service.impl.QuantityParserImpl;
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
import java.text.ParseException;
import java.util.Locale;
import java.util.UUID;

import static com.es.phoneshop.web.constant.ServletConstant.Message.INVALID_NUMBER_FORMAT;
import static com.es.phoneshop.web.constant.ServletConstant.Message.NOT_IN_STOCK;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.ERROR;
import static com.es.phoneshop.web.constant.ServletConstant.RequestParameter.QUANTITY;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    @Mock
    private CartService cartService;
    @Mock
    private QuantityParserImpl quantityParser;
    @Mock
    private RecentlyViewedProductsService recentlyViewedProductsService;
    @InjectMocks
    private ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

    @Before
    public void setup() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(recentlyViewedProductsService.get(any())).thenReturn(new RecentlyViewedProductUnit());
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
    }

    @Test
    public void init_ShouldCreateValidInstanciesOfClasses() throws NoSuchFieldException, IllegalAccessException, ServletException {
        // given
        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);
        ProductServiceImpl productService = ProductServiceImpl.getInstance();
        CartServiceImpl cartService = CartServiceImpl.getInstance();
        RecentlyViewedProductsServiceImpl recentlyViewedService = RecentlyViewedProductsServiceImpl.getInstance();

        // when
        Field productServiceField = ProductDetailsPageServlet.class.getDeclaredField("productService");
        productServiceField.setAccessible(true);
        Field cartServiceField = ProductDetailsPageServlet.class.getDeclaredField("cartService");
        cartServiceField.setAccessible(true);
        Field recentlyViewedServiceField = ProductDetailsPageServlet.class.getDeclaredField("recentlyViewedService");
        recentlyViewedServiceField.setAccessible(true);

        // then
        assertEquals(productService.getClass(), productServiceField.get(servlet).getClass());
        assertEquals(cartService.getClass(), cartServiceField.get(servlet).getClass());
        assertEquals(recentlyViewedService.getClass(), recentlyViewedServiceField.get(servlet).getClass());
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
        verify(request).setAttribute(eq("cart"), any());
        verify(request).setAttribute(eq("recentlyViewed"), any());
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

    @Test
    public void doPost_ValidQuantity_ShouldAddProductToCart() throws ServletException, IOException,
            OutOfStockException, ParseException {
        // given
        final UUID productId = UUID.randomUUID();
        final int quantity = 5;
        final Cart cart = new Cart();
        String redirectLink = request.getContextPath() + "/products/" + productId + "?message=Product added to cart";
        when(request.getPathInfo()).thenReturn("/" + productId);
        when(cartService.get(any())).thenReturn(cart);
        when(request.getParameter(QUANTITY)).thenReturn(String.valueOf(quantity));
        when(quantityParser.parse(eq(String.valueOf(quantity)), any(Locale.class)))
                .thenReturn(quantity);

        // when
        servlet.doPost(request, response);

        // then
        verify(cartService).add(cart, productId, quantity);
        verify(response).sendRedirect(redirectLink);
        verify(request, never()).setAttribute(eq(ERROR), any());
    }

    @Test
    public void doPost_InvalidQuantityFormat_ShouldCatchExceptionAndShowErrorMessage() throws ServletException, IOException,
            ParseException {
        // given
        final UUID productId = UUID.randomUUID();
        final String quantity = "qwerty";
        when(request.getPathInfo()).thenReturn("/" + productId);
        when(request.getParameter(QUANTITY)).thenReturn(quantity);
        when(quantityParser.parse(eq(quantity), any(Locale.class)))
                .thenThrow(NumberFormatException.class);

        // when
        servlet.doPost(request, response);

        // then
        verify(request).setAttribute(eq(ERROR), eq(INVALID_NUMBER_FORMAT));
    }

    @Test
    public void doPost_InvalidStock_ShouldCatchExceptionAndShowErrorMessage() throws ServletException, IOException,
            OutOfStockException, ParseException {
        // given
        final UUID productId = UUID.randomUUID();
        final int quantity = 500;
        final Cart cart = new Cart();
        when(request.getPathInfo()).thenReturn("/" + productId);
        when(cartService.get(any())).thenReturn(cart);
        when(quantityParser.parse(eq(String.valueOf(quantity)), any(Locale.class)))
                .thenReturn(quantity);
        when(request.getParameter(eq(QUANTITY))).thenReturn(String.valueOf(quantity));
        doThrow(OutOfStockException.class).when(cartService).add(cart, productId, quantity);

        // when
        servlet.doPost(request, response);

        // then
        verify(requestDispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
        verify(request).setAttribute(eq(ERROR), eq(NOT_IN_STOCK));
    }

}
