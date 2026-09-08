package com.demo.sandbox.bff.model;

import java.util.List;

/**
 * Respuesta agregada del BFF.
 * Combina datos de customer-api y product-api en un único response.
 * El traceId visible en los logs debe ser idéntico al que aparece
 * en los logs de ambos microservicios downstream.
 */
public record DashboardResponse(
        // String requestTraceId,   // traceId del span activo — para visualización didáctica
        CustomerSummary customer,
        List<ProductSummary> recommendedProducts
) {
    public record CustomerSummary(Long id, String name, String email, String tier) {}
    public record ProductSummary(Long id, String name, Double price, String category) {}
}
