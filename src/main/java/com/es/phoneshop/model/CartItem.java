package com.es.phoneshop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
public class CartItem implements Serializable {
    private Product product;
    private int quantity;

    @Override
    public String toString() {
        return "{ " + product.getCode() +
                ", " + quantity + " }";
    }

}
