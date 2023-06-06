package com.es.phoneshop.dao;

import lombok.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface GenericDao<T> {
    Optional<T> get(UUID id);
    void save(@NonNull T entity);
}
