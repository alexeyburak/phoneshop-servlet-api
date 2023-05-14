package com.es.phoneshop.service.impl;

import com.es.phoneshop.model.Product;
import com.es.phoneshop.model.RecentlyViewedProductUnit;
import com.es.phoneshop.service.ProductService;
import com.es.phoneshop.service.RecentlyViewedProductsService;
import jakarta.servlet.http.HttpSession;

import java.util.Set;
import java.util.UUID;

public class RecentlyViewedProductsServiceImpl implements RecentlyViewedProductsService {
    private static final String RECENTLY_VIEWED_PRODUCT_SESSION_ATTRIBUTE = RecentlyViewedProductsServiceImpl.class.getName() +
            ".product";
    private static final int MAX_RECENTLY_VIEWED_PRODUCTS = 3;
    private final ProductService productService;

    private RecentlyViewedProductsServiceImpl() {
        this.productService = ProductServiceImpl.getInstance();
    }

    private static final class SingletonHolder {
        private static final RecentlyViewedProductsServiceImpl INSTANCE = new RecentlyViewedProductsServiceImpl();
    }

    public static RecentlyViewedProductsServiceImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public RecentlyViewedProductUnit get(HttpSession session) {
        synchronized (session.getId().intern()) {
            RecentlyViewedProductUnit viewedProducts = (RecentlyViewedProductUnit) session
                    .getAttribute(RECENTLY_VIEWED_PRODUCT_SESSION_ATTRIBUTE);

            if (viewedProducts == null) {
                viewedProducts = new RecentlyViewedProductUnit();
                session.setAttribute(RECENTLY_VIEWED_PRODUCT_SESSION_ATTRIBUTE, viewedProducts);
            }
            return viewedProducts;
        }
    }

    @Override
    public void add(UUID productId, RecentlyViewedProductUnit productUnit) {
        synchronized (productUnit) {
            Product product = productService.getProduct(productId);
            Set<Product> products = productUnit.getProducts();

            products.remove(product);

            if (products.size() == MAX_RECENTLY_VIEWED_PRODUCTS) {
                Product oldProduct = products.iterator().next();
                products.remove(oldProduct);
            }

            products.add(product);
        }
    }

}
