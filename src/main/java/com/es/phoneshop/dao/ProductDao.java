package com.es.phoneshop.dao;

import com.es.phoneshop.model.Product;
import com.es.phoneshop.model.ProductSearchCriteria;
import com.es.phoneshop.model.ProductSortCriteria;

import java.util.List;
import java.util.UUID;

public interface ProductDao extends GenericDao<Product> {
    List<Product> findProducts();
    List<Product> findProducts(String query, ProductSortCriteria sort);
    List<Product> findProducts(String query, ProductSearchCriteria search);
    void delete(UUID id);
}
