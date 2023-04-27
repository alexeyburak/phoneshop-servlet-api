package com.es.phoneshop.dao.implementation;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.product.Product;

import java.util.List;

public class ArrayListProductDao implements ProductDao {
    @Override
    public Product getProduct(Long id) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Product> findProducts() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void save(Product product) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void delete(Long id) {
        throw new RuntimeException("Not implemented");
    }
}
