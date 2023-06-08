package com.es.phoneshop.model;

import com.es.phoneshop.model.enums.SearchMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCriteria {
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private SearchMethod method;
}
