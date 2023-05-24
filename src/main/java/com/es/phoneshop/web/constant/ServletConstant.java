package com.es.phoneshop.web.constant;

public interface ServletConstant {
    String REQUEST_DISPATCHER_PRODUCTS = "/WEB-INF/pages/productList.jsp";
    String REQUEST_DISPATCHER_PRODUCT = "/WEB-INF/pages/product.jsp";
    String REQUEST_DISPATCHER_MINI_CART = "/WEB-INF/pages/minicart.jsp";
    String REQUEST_DISPATCHER_CART = "/WEB-INF/pages/cart.jsp";

    interface RequestAttribute {
        String PRODUCTS = "products";
        String RECENTLY_VIEWED = "recentlyViewed";
        String ERROR = "error";
        String PRODUCT = "product";
        String CART = "cart";
        String ERRORS = "errors";
    }

    interface RequestParameter {
        String QUANTITY = "quantity";
        String PRODUCT_ID = "productId";
        String QUERY = "query";
        String SORT = "sort";
        String ORDER = "order";
    }

    interface Message {
        String NOT_A_NUMBER = "Not a number";
        String INVALID_NUMBER_FORMAT = "Invalid number format";
        String NOT_IN_STOCK = "Not available in stock";
        String NEGATIVE_VALUE = "Negative value";
    }

}
