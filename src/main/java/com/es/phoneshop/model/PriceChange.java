package com.es.phoneshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Data
public class PriceChange implements Serializable {
    private LocalDate createdAt;
    private BigDecimal price;
}
