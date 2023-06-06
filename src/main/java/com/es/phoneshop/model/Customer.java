package com.es.phoneshop.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Customer extends Entity {
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}
