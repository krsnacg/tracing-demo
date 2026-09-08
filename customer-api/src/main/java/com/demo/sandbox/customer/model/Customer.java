package com.demo.sandbox.customer.model;

/**
 * Modelo de respuesta del customer-api.
 * Los campos deben coincidir con CustomerSummary del BFF
 * para que la deserialización JSON funcione correctamente.
 */
public record Customer(
        Long id,
        String name,
        String email,
        String tier   // STANDARD, PREMIUM, VIP
) {}
