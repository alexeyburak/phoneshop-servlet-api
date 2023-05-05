package com.es.phoneshop.model;

import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductSortCriteria {
    private SortField sortField;
    private SortOrder sortOrder;
}
