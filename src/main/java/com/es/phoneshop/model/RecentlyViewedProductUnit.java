package com.es.phoneshop.model;

import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public class RecentlyViewedProductUnit {
    private static final int MAX_RECENTLY_VIEWED_PRODUCTS = 3;
    private final Set<Product> products;

    public RecentlyViewedProductUnit() {
        this.products = new LinkedHashSet<>(MAX_RECENTLY_VIEWED_PRODUCTS);
    }

}
