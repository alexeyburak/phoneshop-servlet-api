package com.es.phoneshop.service.implementation;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.implementation.ArrayListProductDaoImpl;
import com.es.phoneshop.model.Product;
import com.es.phoneshop.service.ProductService;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService {
    private final ProductDao productDao;
    private final ReadWriteLock lock;

    public ProductServiceImpl() {
        productDao = new ArrayListProductDaoImpl();
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public Product getProduct(UUID id) {
        return executeReadLock(() ->
                productDao.getProduct(id)
        );
    }

    @Override
    public List<Product> findProducts() {
        return executeReadLock(() ->
                productDao.findProducts()
                        .stream()
                        .filter(this::isPriceNonNull)
                        .filter(this::isInStock)
                        .collect(Collectors.toList())
        );
    }

    private boolean isPriceNonNull(@NonNull Product product) {
        return product.getPrice() != null;
    }

    private boolean isInStock(@NonNull Product product) {
        return product.getStock() > 0;
    }

    private <T> T executeReadLock(@NonNull Supplier<T> supplier) {
        lock.readLock().lock();
        try {
            return supplier.get();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(Product product) {
        executeWriteLock(() -> productDao.save(product));
    }

    @Override
    public void delete(UUID id) {
        executeWriteLock(() -> productDao.delete(id));
    }

    private void executeWriteLock(@NonNull Runnable runnable) {
        lock.writeLock().lock();
        try {
            runnable.run();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
