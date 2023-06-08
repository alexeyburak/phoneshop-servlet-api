package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ProductDaoImpl;
import com.es.phoneshop.model.ProductSearchCriteria;
import com.es.phoneshop.model.ProductSortCriteria;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.Product;
import com.es.phoneshop.service.ProductService;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductDao productDao;

    private ProductServiceImpl() {
        productDao = ProductDaoImpl.getInstance();
    }

    private static final class SingletonHolder {
        private static final ProductServiceImpl INSTANCE = new ProductServiceImpl();
    }

    public static ProductServiceImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public Product getProduct(@NonNull UUID id) {
        LOGGER.debug("Getting product. Id: {}", id);

        return productDao.get(id)
                .orElseThrow(ProductNotFoundException::new);
    }

    @Override
    public List<Product> findProducts() {
        return productsFilter(
                productDao.findProducts()
        );
    }

    @Override
    public List<Product> findProducts(String query, ProductSortCriteria sort) {
        return productsFilter(
                productDao.findProducts(query, sort)
        );
    }

    @Override
    public List<Product> findProducts(String query, ProductSearchCriteria search) {
        return productsFilter(
                productDao.findProducts(query, search)
        );
    }

    @Override
    public void save(Product product) {
        productDao.save(product);
        LOGGER.debug("Save product. Id: {}", product.getId());
    }

    @Override
    public void delete(@NonNull UUID id) {
        productDao.delete(id);
        LOGGER.debug("Delete product. Id: {}", id);
    }

    private List<Product> productsFilter(List<Product> products) {
        return products.stream()
                .filter(product -> product.getPrice() != null)
                .filter(product -> product.getStock() > 0)
                .collect(Collectors.toList());
    }

}
