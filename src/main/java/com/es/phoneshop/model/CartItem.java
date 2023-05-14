package com.es.phoneshop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CartItem {
    private Product product;
    private int quantity;

    @Override
    public String toString() {
        return "{ " + product.getCode() +
                ", " + quantity + " }";
    }

}
