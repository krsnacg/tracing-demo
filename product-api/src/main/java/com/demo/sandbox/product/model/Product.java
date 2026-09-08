package com.demo.sandbox.product.model;

public record Product(
        Long id,
        String name,
        Double price,
        String category
) {}
