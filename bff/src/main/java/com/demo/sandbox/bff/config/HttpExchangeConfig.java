package com.demo.sandbox.bff.config;

import com.demo.sandbox.bff.client.CustomerClient;
import com.demo.sandbox.bff.client.ProductClient;
import com.demo.sandbox.bff.config.properties.RestClientName;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Registra los proxies generados para los @HttpExchange clients.
 *
 * Cada proxy se construye sobre el RestClient correspondiente
 * (customerClient / productClient), que ya tiene el interceptor de tracing.
 * No se necesita ninguna configuración adicional de tracing aquí.
 */
@Configuration
@RequiredArgsConstructor
public class HttpExchangeConfig {

    // private final ClientLoggingInterceptor loggingInterceptor;

    @Bean
    public CustomerClient customerApiClient(Map<RestClientName, RestClient.Builder> restClientBuilders) {
        RestClient customerClient = restClientBuilders.get(RestClientName.CUSTOMER_API)
            // .requestInterceptor(loggingInterceptor)
            .build();

        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(customerClient))
                .build()
                .createClient(CustomerClient.class);
    }

    @Bean
    public ProductClient productApiClient(Map<RestClientName, RestClient.Builder> restClientBuilders) {
        RestClient productClient = restClientBuilders.get(RestClientName.PRODUCT_API)
            // .requestInterceptor(loggingInterceptor)
            .build();

        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(productClient))
                .build()
                .createClient(ProductClient.class);
    }
}
