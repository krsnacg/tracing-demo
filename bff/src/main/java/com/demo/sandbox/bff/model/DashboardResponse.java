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
        CustomerAdditionalInfo additionalInfo,
        List<ProductSummary> recommendedProducts
) {
    public record CustomerSummary(Long id, String name, String userEmail, String tier) {}
    public record CustomerAdditionalInfo(Integer loyaltyPoints, String accountStatus) {}
    public record ProductSummary(Long id, String name, Double price, String category) {}
}
