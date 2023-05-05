package com.es.phoneshop.dao.implementation;

import com.es.phoneshop.comparator.ProductKeywordMatchesComparator;
import com.es.phoneshop.comparator.ProductSortingComparator;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.ProductSortCriteria;
import com.es.phoneshop.model.Product;
import com.es.phoneshop.service.Procedure;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ProductDaoImpl implements ProductDao {
    private final ReadWriteLock lock;
    private List<Product> products;

    private ProductDaoImpl() {
        this.products = new ArrayList<>();
        lock = new ReentrantReadWriteLock();
    }

    private static final class SingletonHolder {
        private static final ProductDaoImpl INSTANCE = new ProductDaoImpl();
    }

    public static ProductDaoImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public Optional<Product> getProduct(UUID id) {
        return executeReadLock(() ->
                products.stream()
                        .filter(product -> id.equals(product.getId()))
                        .findAny()
        );
    }

	@Override
	public List<Product> findProducts(String query, ProductSortCriteria sort) {
		String[] keywords = query.split("\\s+");

        Comparator<Product> comparator = getProductComparator(keywords, sort);

		return executeReadLock(() ->
                products.stream()
                        .filter(productKeywordMatchesFilter(keywords))
                        .sorted(comparator)
                        .collect(Collectors.toList())
        );
	}

    @Override
    public void save(@NonNull Product product) {
        product.setId(UUID.randomUUID());

        executeWriteLock(() ->
                products.add(product)
        );
    }

    @Override
    public void delete(UUID id) {
        executeWriteLock(() ->
                products.stream()
                        .filter(product -> id.equals(product.getId()))
                        .findAny()
                        .ifPresent(products::remove)
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

	private <T> T executeReadLock(@NonNull Supplier<T> supplier) {
		lock.readLock().lock();
		try {
			return supplier.get();
		} finally {
			lock.readLock().unlock();
		}
	}

    private void executeWriteLock(@NonNull Procedure procedure) {
        lock.writeLock().lock();
        try {
            procedure.get();
        } finally {
            lock.writeLock().unlock();
        }
    }

}
