package com.es.phoneshop.service.implementation;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.implementation.ProductDaoImpl;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.Product;
import com.es.phoneshop.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductDao productDao;

    public ProductServiceImpl() {
        productDao = ProductDaoImpl.getInstance();
    }

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public Product getProduct(@NonNull UUID id) {
        LOGGER.debug("Getting product. Id: {}", id);

        return productDao.getProduct(id)
                .orElseThrow(ProductNotFoundException::new);
    }

    @Override
    public List<Product> findProducts() {
        return productDao.findProducts()
                .stream()
                .filter(product -> product.getPrice() != null)
                .filter(product -> product.getStock() > 0)
                .collect(Collectors.toList());
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

}
