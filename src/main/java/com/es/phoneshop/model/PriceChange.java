package com.es.phoneshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Data
public class PriceChange implements Serializable {
    @Serial
    private static final long serialVersionUID = 794731707128479850L;

    private LocalDate createdAt;
    private BigDecimal price;
}
