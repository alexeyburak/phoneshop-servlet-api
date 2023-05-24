package com.es.phoneshop.web;

import com.es.phoneshop.model.Cart;
import com.es.phoneshop.model.ProductSortCriteria;
import com.es.phoneshop.model.RecentlyViewedProductUnit;
import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.RecentlyViewedProductsService;
import com.es.phoneshop.service.impl.ProductServiceImpl;
import com.es.phoneshop.service.impl.QuantityParserImpl;
import com.es.phoneshop.service.impl.RecentlyViewedProductsServiceImpl;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Locale;
import java.util.UUID;

import static com.es.phoneshop.web.constant.ServletConstant.Message.INVALID_NUMBER_FORMAT;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.ERROR;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.PRODUCTS;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.RECENTLY_VIEWED;
import static com.es.phoneshop.web.constant.ServletConstant.RequestParameter.ORDER;
import static com.es.phoneshop.web.constant.ServletConstant.RequestParameter.PRODUCT_ID;
import static com.es.phoneshop.web.constant.ServletConstant.RequestParameter.QUANTITY;
import static com.es.phoneshop.web.constant.ServletConstant.RequestParameter.QUERY;
import static com.es.phoneshop.web.constant.ServletConstant.RequestParameter.SORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    private HttpSession httpSession;
    @Mock
    private CartService cartService;
    @Mock
    private QuantityParserImpl quantityParser;
    @Mock
    private RecentlyViewedProductsService recentlyViewedProductsService;
    @InjectMocks
    private ProductListPageServlet servlet = new ProductListPageServlet();

    @Before
    public void setup() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getSession()).thenReturn(httpSession);
        when(recentlyViewedProductsService.get(any())).thenReturn(new RecentlyViewedProductUnit());
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
        // when
        servlet.doGet(request, response);

        // then
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq(PRODUCTS), any());
        verify(request).setAttribute(eq(RECENTLY_VIEWED), any());
    }

    @Test
    public void parseSortCriteria_ValidParameters_ShouldReturnProductSortCriteriaObject() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        when(request.getParameter(SORT)).thenReturn("price");
        when(request.getParameter(ORDER)).thenReturn("asc");
        Method getSortCriteriaMethod = ProductListPageServlet.class.getDeclaredMethod("parseSortCriteria", HttpServletRequest.class);
        getSortCriteriaMethod.setAccessible(true);

        // when
        ProductSortCriteria criteria = (ProductSortCriteria) getSortCriteriaMethod.invoke(servlet, request);

        // then
        assertEquals(SortField.price, criteria.getSortField());
        assertEquals(SortOrder.asc, criteria.getSortOrder());
    }

    @Test
    public void parseSortCriteria_NullParameters_ShouldReturnNull() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        when(request.getParameter(SORT)).thenReturn(null);
        when(request.getParameter(ORDER)).thenReturn(null);
        Method getSortCriteriaMethod = ProductListPageServlet.class.getDeclaredMethod("parseSortCriteria", HttpServletRequest.class);
        getSortCriteriaMethod.setAccessible(true);

        // when
        ProductSortCriteria criteria = (ProductSortCriteria) getSortCriteriaMethod.invoke(servlet, request);

        // then
        assertNull(criteria);
    }

    @Test
    public void doPost_ValidParameters_ShouldRedirectToSuccessPageWithSorting() throws ServletException, IOException {
        // given
        final Cart cart = new Cart();
        final UUID id = UUID.randomUUID();
        final SortField sort = SortField.price;
        final SortOrder order = SortOrder.asc;
        when(request.getParameter(SORT)).thenReturn(sort.toString());
        when(request.getParameter(ORDER)).thenReturn(order.toString());
        when(request.getParameter(PRODUCT_ID)).thenReturn(id.toString());
        when(request.getParameter(QUANTITY)).thenReturn("2");
        when(cartService.get(httpSession)).thenReturn(cart);

        // when
        servlet.doPost(request, response);

        // then
        verify(response).sendRedirect(request.getContextPath() +
                "/products?message=Product added to cart&sort=" + sort +
                "&order=" + order);
    }

    @Test
    public void doPost_ValidParameters_ShouldRedirectToSuccessPageWithQuery() throws ServletException, IOException {
        // given
        final Cart cart = new Cart();
        final UUID id = UUID.randomUUID();
        final String query = "sam iii";
        when(request.getParameter(QUERY)).thenReturn(query);
        when(request.getParameter(ORDER)).thenReturn(null);
        when(request.getParameter(PRODUCT_ID)).thenReturn(id.toString());
        when(request.getParameter(QUANTITY)).thenReturn("2");
        when(cartService.get(httpSession)).thenReturn(cart);

        // when
        servlet.doPost(request, response);

        // then
        verify(response).sendRedirect(request.getContextPath() +
                "/products?message=Product added to cart&query=" + query);
    }

    @Test
    public void doPost_ValidParameters_ShouldRedirectToSuccessPage() throws ServletException, IOException {
        // given
        final Cart cart = new Cart();
        final UUID id = UUID.randomUUID();
        when(request.getParameter(SORT)).thenReturn(null);
        when(request.getParameter(ORDER)).thenReturn(null);
        when(request.getParameter(PRODUCT_ID)).thenReturn(id.toString());
        when(request.getParameter(QUANTITY)).thenReturn("2");
        when(cartService.get(httpSession)).thenReturn(cart);

        // when
        servlet.doPost(request, response);

        // then
        verify(response).sendRedirect(request.getContextPath() +
                "/products?message=Product added to cart");
    }

    @Test
    public void doPost_NegativeQuantity_ShouldRedirectToErrorPage() throws ServletException, IOException, ParseException {
        // given
        final UUID id = UUID.randomUUID();
        final String quantity = "-22";
        when(request.getParameter(PRODUCT_ID)).thenReturn(id.toString());
        when(request.getParameter(QUANTITY)).thenReturn(quantity);
        when(quantityParser.parse(eq(quantity), any(Locale.class)))
                .thenThrow(NumberFormatException.class);

        // when
        servlet.doPost(request, response);

        // then
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq(ERROR), eq(INVALID_NUMBER_FORMAT));
        verify(response, never()).sendRedirect(request.getContextPath() + "/products?message=Product added to cart");
    }

    @Test
    public void doPost_InvalidFormatQuantity_ShouldRedirectToErrorPage() throws ServletException, IOException, ParseException {
        // given
        final UUID id = UUID.randomUUID();
        final String quantity = "wasd";
        when(request.getParameter(PRODUCT_ID)).thenReturn(id.toString());
        when(request.getParameter(QUANTITY)).thenReturn(quantity);
        when(quantityParser.parse(eq(quantity), any(Locale.class)))
                .thenThrow(NumberFormatException.class);

        // when
        servlet.doPost(request, response);

        // then
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq(ERROR), eq(INVALID_NUMBER_FORMAT));
        verify(response, never()).sendRedirect(request.getContextPath() + "/products?message=Product added to cart");
    }

}
