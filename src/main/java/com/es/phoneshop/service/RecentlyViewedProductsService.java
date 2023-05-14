package com.es.phoneshop.service;

import com.es.phoneshop.model.RecentlyViewedProductUnit;
import jakarta.servlet.http.HttpSession;

import java.util.UUID;

public interface RecentlyViewedProductsService {
    RecentlyViewedProductUnit get(HttpSession session);
    void add(UUID productId, RecentlyViewedProductUnit productUnit);
}
