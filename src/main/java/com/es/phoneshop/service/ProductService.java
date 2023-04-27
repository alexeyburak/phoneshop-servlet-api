package com.es.phoneshop.service;

import com.es.phoneshop.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product getProduct(UUID id);
    List<Product> findProducts();
    void save(Product product);
    void delete(UUID id);
}
