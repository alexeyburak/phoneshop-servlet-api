package com.es.phoneshop.comparator;

import com.es.phoneshop.model.Product;

import java.util.Arrays;
import java.util.Comparator;

public class ProductKeywordMatchesComparator implements Comparator<Product> {
    private final String[] keywords;

    public ProductKeywordMatchesComparator(String[] keywords) {
        this.keywords = keywords;
    }

    @Override
    public int compare(Product p1, Product p2) {
        int p1Matches = productMatchesCounter(p1);
        int p2Matches = productMatchesCounter(p2);

        return Integer.compare(p2Matches, p1Matches);
    }

    private int productMatchesCounter(Product product) {
        return (int) Arrays.stream(keywords)
                .filter(keyword -> containsIgnoreCase((product.getDescription()), keyword))
                .count();
    }

    private boolean containsIgnoreCase(String str, String searchStr) {
        return str != null &&
                searchStr != null &&
                str.toLowerCase().contains(searchStr.toLowerCase());
    }
}
