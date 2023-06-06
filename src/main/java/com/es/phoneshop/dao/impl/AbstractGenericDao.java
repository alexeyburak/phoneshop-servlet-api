package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.GenericDao;
import com.es.phoneshop.model.Entity;
import com.es.phoneshop.service.Procedure;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public abstract class AbstractGenericDao<T extends Entity> implements GenericDao<T> {
    private final ReadWriteLock lock;
    protected List<T> items;

    public AbstractGenericDao() {
        this.items = new ArrayList<>();
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public Optional<T> get(UUID id) {
        return executeReadLock(() ->
                items.stream()
                        .filter(item -> id.equals(item.getId()))
                        .findAny()
        );
    }

    @Override
    public void save(@NonNull T entity) {
        entity.setId(UUID.randomUUID());

        executeWriteLock(() ->
                items.add(entity)
        );
    }

    protected <V> V executeReadLock(@NonNull Supplier<V> supplier) {
        lock.readLock().lock();
        try {
            return supplier.get();
        } finally {
            lock.readLock().unlock();
        }
    }

    protected void executeWriteLock(@NonNull Procedure procedure) {
        lock.writeLock().lock();
        try {
            procedure.get();
        } finally {
            lock.writeLock().unlock();
        }
    }

}
