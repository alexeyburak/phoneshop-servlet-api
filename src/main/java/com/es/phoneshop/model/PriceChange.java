package com.es.phoneshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Data
public class PriceChange {
    private LocalDate createdAt;
    private BigDecimal price;
}
