package com.es.phoneshop.dao.impl;

import com.es.phoneshop.comparator.ProductKeywordMatchesComparator;
import com.es.phoneshop.comparator.ProductSortingComparator;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.Product;
import com.es.phoneshop.model.ProductSortCriteria;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProductDaoImpl extends AbstractGenericDao<Product> implements ProductDao {

    private ProductDaoImpl() {
    }

    private static final class SingletonHolder {
        private static final ProductDaoImpl INSTANCE = new ProductDaoImpl();
    }

    public static ProductDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }


    @Override
    public List<Product> findProducts(String query, ProductSortCriteria sort) {
        String[] keywords = query.split("\\s+");

        Comparator<Product> comparator = getProductComparator(keywords, sort);

        return executeReadLock(() ->
                items.stream()
                        .filter(productKeywordMatchesFilter(keywords))
                        .sorted(comparator)
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

    private Predicate<Product> productKeywordMatchesFilter(String[] keywords) {
        return p -> Arrays.stream(keywords)
                .map(String::toLowerCase)
                .anyMatch(p.getDescription().toLowerCase()::contains);
    }

}
