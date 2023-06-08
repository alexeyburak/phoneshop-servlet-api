package com.es.phoneshop.service;

import com.es.phoneshop.model.Product;
import com.es.phoneshop.model.ProductSearchCriteria;
import com.es.phoneshop.model.ProductSortCriteria;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product getProduct(@NonNull UUID id);
    List<Product> findProducts();
    List<Product> findProducts(String query, ProductSortCriteria sort);
    List<Product> findProducts(String query, ProductSearchCriteria search);
    void save(Product product);
    void delete(@NonNull UUID id);
}
