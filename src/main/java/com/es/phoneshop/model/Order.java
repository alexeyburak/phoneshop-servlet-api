package com.es.phoneshop.model;

import com.es.phoneshop.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Order extends Entity {
    private List<CartItem> items;
    private int totalQuantity;

    private BigDecimal subTotal;
    private BigDecimal deliveryCost;
    private BigDecimal totalCost;

    private Customer customer;
    private LocalDate deliveryDate;
    private PaymentMethod paymentMethod;

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(UUID id, List<CartItem> items, int totalQuantity, BigDecimal totalCost, BigDecimal deliveryCost,
                 BigDecimal subTotal, Customer customer, LocalDate deliveryDate, PaymentMethod paymentMethod) {
        this.id = id;
        this.items = items;
        this.totalQuantity = totalQuantity;
        this.totalCost = totalCost;
        this.deliveryCost = deliveryCost;
        this.subTotal = subTotal;
        this.customer = customer;
        this.deliveryDate = deliveryDate;
        this.paymentMethod = paymentMethod;
    }
}
