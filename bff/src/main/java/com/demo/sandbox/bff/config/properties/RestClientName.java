package com.demo.sandbox.bff.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RestClientName {
    CUSTOMER_API("customer-api"),
    PRODUCT_API("product-api");

    private String name;
}
