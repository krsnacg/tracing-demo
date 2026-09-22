package com.demo.sandbox.bff.config.http;

import org.springframework.boot.http.client.HttpComponentsClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.autoconfigure.ClientHttpRequestFactoryBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.service.registry.ImportHttpServices;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

import com.demo.sandbox.bff.config.http.constants.ClientGroups;
import com.demo.sandbox.bff.config.http.interceptor.DynamicHeadersInterceptor;
import com.demo.sandbox.bff.config.http.interceptor.ExecutionTimeInterceptor;
import com.demo.sandbox.bff.shared.client.AuthApiClient;
import com.demo.sandbox.bff.shared.client.ErrorApiClient;
import com.demo.sandbox.bff.shared.client.ProductClient;
import com.demo.sandbox.bff.shared.client.SlowApiClient;
import com.demo.sandbox.bff.shared.client.customer.CustomerClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Configuration
@ImportHttpServices(
    group = ClientGroups.CUSTOMER_API, 
    basePackageClasses = {CustomerClient.class})
@ImportHttpServices(
    group = ClientGroups.PRODUCT_API, 
    types = {ProductClient.class})
@ImportHttpServices(
    group = ClientGroups.AUTH_API,
    types = {AuthApiClient.class})
@ImportHttpServices(
    group = ClientGroups.ERROR_API,
    types = {ErrorApiClient.class})
@ImportHttpServices(
    group = ClientGroups.SLOW_API,
    types = {SlowApiClient.class})
@RequiredArgsConstructor
public class HttpClientsConfig {

    private final DynamicHeadersInterceptor dynamicHeadersInterceptor;

    @Bean
    ClientHttpRequestFactoryBuilderCustomizer
        <HttpComponentsClientHttpRequestFactoryBuilder> poolCustomizer() {
        return builder -> builder
            .withConnectionManagerCustomizer(manager -> manager
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(100));
    }

    @Bean 
    RestClientHttpServiceGroupConfigurer groupConfigurer(Logbook logbook) {
        return groups -> {

            groups.filterByName(ClientGroups.AUTH_API)
                .forEachClient((group, clientBuilder) -> clientBuilder
                    .defaultHeaders(this::buildStaticHttpHeaders));

            groups.filterByName(ClientGroups.ERROR_API)
                .forEachClient((group, clientBuilder) -> clientBuilder
                    .defaultStatusHandler(
                        HttpStatusCode::isError,(request, response) -> {
                            throw new ResponseStatusException(response.getStatusCode(),
                            String.format("Simulated error ocurred in %s client", group.name()));
                        }));

            groups.filterByName(ClientGroups.SLOW_API)
                .forEachClient((group, builder) -> builder
                    .requestInterceptor(new ExecutionTimeInterceptor(group.name())));

            groups.filterByName(ClientGroups.CUSTOMER_API, ClientGroups.PRODUCT_API)
                .forEachClient((group, clientBuilder) -> clientBuilder
                    .requestInterceptor(dynamicHeadersInterceptor));

            groups.forEachClient((group, clientBuilder) -> 
                clientBuilder.requestInterceptor(
                    new LogbookClientHttpRequestInterceptor(logbook)));
        };
    }

    private void buildStaticHttpHeaders(HttpHeaders headers) {
        headers.add("X-Demo-Header", "poc-value");
    }
}