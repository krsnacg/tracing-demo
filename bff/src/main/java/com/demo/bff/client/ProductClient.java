package com.demo.bff.client;

import com.demo.bff.model.DashboardResponse.ProductSummary;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

/**
 * Declarative HTTP client para product-api.
 * Misma mecánica que CustomerClient — el tracing viaja en el RestClient subyacente.
 */
@HttpExchange
public interface ProductClient {

    @GetExchange("/products")
    List<ProductSummary> getProducts();

    @GetExchange("/products/{id}")
    ProductSummary getProduct(@PathVariable Long id);
}
