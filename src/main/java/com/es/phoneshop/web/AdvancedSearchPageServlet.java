package com.es.phoneshop.web;

import com.es.phoneshop.model.ProductSearchCriteria;
import com.es.phoneshop.model.enums.SearchMethod;
import com.es.phoneshop.model.enums.SearchState;
import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.impl.ProductServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static com.es.phoneshop.web.constant.ServletConstant.Message.INVALID_NUMBER_FORMAT;
import static com.es.phoneshop.web.constant.ServletConstant.Message.NEGATIVE_VALUE;
import static com.es.phoneshop.web.constant.ServletConstant.REQUEST_DISPATCHER_ADVANCED_SEARCH;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.ERRORS;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.MAX_PRICE;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.MIN_PRICE;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.PRODUCTS;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.SEARCH_METHOD;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute.SEARCH_METHODS;
import static com.es.phoneshop.web.constant.ServletConstant.RequestParameter.QUERY;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class AdvancedSearchPageServlet extends HttpServlet {
    private ProductService productService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = ProductServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> errors = new HashMap<>();
        ProductSearchCriteria search = parseSearchCriteria(request, errors);

        setValidAttributes(request, errors, search);

        request.getRequestDispatcher(REQUEST_DISPATCHER_ADVANCED_SEARCH).forward(request, response);
    }

    private ProductSearchCriteria parseSearchCriteria(HttpServletRequest request, Map<String, String> errors) {
        ProductSearchCriteria criteria = new ProductSearchCriteria();
        return isValidSearchCriteria(request, errors, criteria) ? criteria : null;
    }

    private boolean isValidSearchCriteria(HttpServletRequest request, Map<String, String> errors,
                                          ProductSearchCriteria criteria) {
        return parseSearchMethod(request, criteria::setMethod) &&
                parsePrice(request, MIN_PRICE, errors, criteria::setMinPrice) &&
                parsePrice(request, MAX_PRICE, errors, criteria::setMaxPrice);
    }

    private boolean parseSearchMethod(HttpServletRequest request, Consumer<SearchMethod> consumer) {
        String searchMethod = request.getParameter(SEARCH_METHOD);
        if (isEmpty(searchMethod)) {
            return false;
        }
        consumer.accept(SearchMethod.valueOf(searchMethod));
        return true;
    }

    private boolean parsePrice(HttpServletRequest request, String parameter, Map<String, String> errors,
                               Consumer<BigDecimal> consumer) {
        String value = request.getParameter(parameter);
        if (isEmpty(value)) {
            return true;
        }
        return validatePrice(parameter, errors, consumer, value);
    }

    private boolean validatePrice(String parameter, Map<String, String> errors,
                                  Consumer<BigDecimal> consumer, String value) {
        try {
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble(value));
            validateIfNegativePrice(price);
            consumer.accept(price);
            return true;
        } catch (NumberFormatException e) {
            errors.put(parameter, INVALID_NUMBER_FORMAT);
            return false;
        } catch (IllegalArgumentException e) {
            errors.put(parameter, NEGATIVE_VALUE);
            return false;
        }
    }

    private void validateIfNegativePrice(BigDecimal price) throws IllegalArgumentException {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException();
        }
    }

    private void setValidAttributes(HttpServletRequest request, Map<String, String> errors,
                                    ProductSearchCriteria search) {
        final SearchState state = getSearchState(request.getParameterMap(), errors, search);

        switch (state) {
            case NO_PARAMETERS -> request.setAttribute(PRODUCTS, new ArrayList<>());
            case EMPTY -> request.setAttribute(PRODUCTS, productService.findProducts());
            case INVALID_PARAMETERS -> {
                request.setAttribute(PRODUCTS, new ArrayList<>());
                request.setAttribute(ERRORS, errors);
            }
            case VALID -> {
                String query = parseQuery(request);
                request.setAttribute(PRODUCTS, productService.findProducts(query, search));
            }
        }

        request.setAttribute(SEARCH_METHODS, Arrays.asList(SearchMethod.values()));
    }

    private SearchState getSearchState(Map<String, String[]> parameterMap, Map<String, String> errors,
                                       ProductSearchCriteria search) {
        if (parameterMap.isEmpty()) {
            return SearchState.NO_PARAMETERS;
        } else if (Objects.isNull(search)) {
            return errors.isEmpty() ? SearchState.EMPTY : SearchState.INVALID_PARAMETERS;
        } else {
            return SearchState.VALID;
        }
    }

    private String parseQuery(HttpServletRequest request) {
        return Optional.ofNullable(request.getParameter(QUERY))
                .orElse(EMPTY);
    }

}
