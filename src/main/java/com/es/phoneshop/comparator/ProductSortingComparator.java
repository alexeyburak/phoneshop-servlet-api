package com.es.phoneshop.comparator;

import com.es.phoneshop.model.ProductSortCriteria;
import com.es.phoneshop.model.Product;
import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import lombok.NonNull;

import java.util.Comparator;

public class ProductSortingComparator implements Comparator<Product> {
    private final SortField sortField;
    private final SortOrder sortOrder;

    public ProductSortingComparator(@NonNull ProductSortCriteria sortCriteria) {
        this.sortField = sortCriteria.getSortField();
        this.sortOrder = sortCriteria.getSortOrder();
    }

    @Override
    public int compare(Product o1, Product o2) {
        int comparison = switch (sortField) {
            case description -> o1.getDescription()
                    .compareToIgnoreCase(o2.getDescription());
            case price -> o1.getPrice()
                    .compareTo(o2.getPrice());
        };

        return sortOrder == SortOrder.asc ? comparison : -comparison;
    }

}
