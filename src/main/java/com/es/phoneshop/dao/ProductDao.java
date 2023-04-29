package com.es.phoneshop.dao;

import com.es.phoneshop.model.Product;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductDao {
    Optional<Product> getProduct(UUID id);
    List<Product> findProducts();
    void save(@NonNull Product product);
    void delete(UUID id);
}
