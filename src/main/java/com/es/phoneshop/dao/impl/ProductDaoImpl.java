package com.es.phoneshop.dao.impl;

import com.es.phoneshop.comparator.ProductKeywordMatchesComparator;
import com.es.phoneshop.comparator.ProductSortingComparator;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.Product;
import com.es.phoneshop.model.ProductSearchCriteria;
import com.es.phoneshop.model.ProductSortCriteria;
import com.es.phoneshop.model.enums.SearchMethod;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProductDaoImpl extends AbstractGenericDao<Product> implements ProductDao {
    private static final String STRING_SPLIT_REGEX = "\\s+";

    private ProductDaoImpl() {
    }

    private static final class SingletonHolder {
        private static final ProductDaoImpl INSTANCE = new ProductDaoImpl();
    }

    public static ProductDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public List<Product> findProducts() {
        return executeReadLock(() ->
                items
        );
    }

    @Override
    public List<Product> findProducts(String query, ProductSortCriteria sort) {
        String[] keywords = query.split(STRING_SPLIT_REGEX);

        Comparator<Product> comparator = getProductComparator(keywords, sort);

        return executeReadLock(() ->
                items.stream()
                        .filter(productKeywordMatchesFilter(keywords))
                        .sorted(comparator)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<Product> findProducts(String query, ProductSearchCriteria search) {
        final SearchMethod method = search.getMethod();
        final BigDecimal minPrice = search.getMinPrice();
        final BigDecimal maxPrice = search.getMaxPrice();

        String[] keywords = query.split(STRING_SPLIT_REGEX);

        return executeReadLock(() ->
                items.stream()
                        .filter(getProductKeywordMatchesFilter(method, keywords))
                        .filter(productPriceMatchesFilter(minPrice, maxPrice))
                        .sorted(new ProductKeywordMatchesComparator(keywords))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void delete(UUID id) {
        executeWriteLock(() ->
                items.stream()
                        .filter(product -> id.equals(product.getId()))
                        .findAny()
                        .ifPresent(items::remove)
        );
    }

    private Comparator<Product> getProductComparator(String[] keywords, ProductSortCriteria sort) {
        return sort != null ? new ProductSortingComparator(sort) :
                new ProductKeywordMatchesComparator(keywords);
    }

    private Predicate<Product> getProductKeywordMatchesFilter(SearchMethod method, String[] keywords) {
        return (method == SearchMethod.ANY_WORD) ? productKeywordMatchesFilter(keywords) :
                productAllKeywordMatchesFilter(keywords);
    }

    private Predicate<Product> productKeywordMatchesFilter(String[] keywords) {
        return p -> Arrays.stream(keywords)
                .map(String::toLowerCase)
                .anyMatch(p.getDescription().toLowerCase()::contains);
    }

    private Predicate<Product> productAllKeywordMatchesFilter(String[] keywords) {
        return p -> Arrays.stream(keywords)
                .map(String::toLowerCase)
                .allMatch(p.getDescription().toLowerCase()::contains);
    }

    private Predicate<Product> productPriceMatchesFilter(BigDecimal minPrice, BigDecimal maxPrice) {
        BigDecimal min = Objects.requireNonNullElse(minPrice, BigDecimal.ZERO);
        BigDecimal max = Objects.requireNonNullElse(maxPrice, BigDecimal.valueOf(Double.MAX_VALUE));
        return p -> p.getPrice().compareTo(min) >= 0 && p.getPrice().compareTo(max) <= 0;
    }

}
