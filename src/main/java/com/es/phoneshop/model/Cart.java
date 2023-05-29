package com.es.phoneshop.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Cart implements Serializable {
    @Serial
    private static final long serialVersionUID = 7777074232909829751L;

    private final List<CartItem> items;
    private int totalQuantity;
    private BigDecimal totalCost;

    public Cart() {
        this.items = new ArrayList<>();
    }

}
