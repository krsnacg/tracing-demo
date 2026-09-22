package com.demo.sandbox.bff.config;

import org.springframework.boot.http.client.HttpComponentsClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.autoconfigure.ClientHttpRequestFactoryBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

import com.demo.sandbox.bff.client.CustomerClient;
import com.demo.sandbox.bff.client.ProductClient;

@Configuration
@ImportHttpServices(group = ClientGroups.CUSTOMER_API, types = {CustomerClient.class})
@ImportHttpServices(group = ClientGroups.PRODUCT_API, types = {ProductClient.class})
public class HttpClientsConfig {

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
        return groups -> groups.forEachClient((group, clientBuilder) -> 
            clientBuilder.requestInterceptor(
                new LogbookClientHttpRequestInterceptor(logbook)));
    }
}