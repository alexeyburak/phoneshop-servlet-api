package com.es.phoneshop.service;

import com.es.phoneshop.model.ProductSortCriteria;
import com.es.phoneshop.model.Product;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product getProduct(@NonNull UUID id);
    List<Product> findProducts(String query, ProductSortCriteria sort);
    void save(Product product);
    void delete(@NonNull UUID id);
}
