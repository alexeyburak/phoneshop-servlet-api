package com.es.phoneshop.web;

import com.es.phoneshop.model.ProductSortCriteria;
import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.RecentlyViewedProductsService;
import com.es.phoneshop.service.impl.ProductServiceImpl;
import com.es.phoneshop.service.impl.RecentlyViewedProductsServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class ProductListPageServlet extends HttpServlet {
    private static final String REQUEST_ATTRIBUTE_PRODUCTS = "products";
    private static final String REQUEST_ATTRIBUTE_RECENTLY_VIEWED = "recentlyViewed";
    private static final String REQUEST_DISPATCHER_PRODUCTS = "/WEB-INF/pages/productList.jsp";
    private static final String REQUEST_PARAM_QUERY = "query";
    private static final String REQUEST_PARAM_SORT = "sort";
    private static final String REQUEST_PARAM_ORDER = "order";
    private ProductService productService;
    private RecentlyViewedProductsService recentlyViewedService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ProductServiceImpl.getInstance();
        recentlyViewedService = RecentlyViewedProductsServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = Optional.ofNullable(request.getParameter(REQUEST_PARAM_QUERY))
                .orElse(EMPTY);
        ProductSortCriteria sortCriteria = getSortCriteria(request);

        request.setAttribute(REQUEST_ATTRIBUTE_PRODUCTS, productService.findProducts(query, sortCriteria));
        request.setAttribute(REQUEST_ATTRIBUTE_RECENTLY_VIEWED, recentlyViewedService.get(request.getSession()).getProducts());
        request.getRequestDispatcher(REQUEST_DISPATCHER_PRODUCTS).forward(request, response);
    }

    private ProductSortCriteria getSortCriteria(HttpServletRequest request) {
        String sortField = request.getParameter(REQUEST_PARAM_SORT);
        String sortOrder = request.getParameter(REQUEST_PARAM_ORDER);

        return sortField != null && sortOrder != null ? ProductSortCriteria.builder()
                .sortField(SortField.valueOf(sortField))
                .sortOrder(SortOrder.valueOf(sortOrder))
                .build() : null;
    }

}
