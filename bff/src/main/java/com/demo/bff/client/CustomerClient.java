package com.demo.bff.client;

import com.demo.bff.model.DashboardResponse.CustomerSummary;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * Declarative HTTP client para customer-api usando @HttpExchange.
 *
 * Spring genera el proxy en tiempo de compilación/arranque.
 * Internamente usa el RestClient inyectado (customerClient), que ya
 * tiene el interceptor de tracing — por eso el traceId se propaga
 * sin ninguna anotación adicional aquí.
 */
@HttpExchange
public interface CustomerClient {

    @GetExchange("/customers/{id}")
    CustomerSummary getCustomer(@PathVariable Long id);
}
