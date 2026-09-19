package com.demo.sandbox.bff.config;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

import com.demo.sandbox.bff.config.properties.RestClientConfigProperties;
import com.demo.sandbox.bff.config.properties.RestClientName;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.json.JsonMapper;

/**
 * Configuración de clientes HTTP del BFF.
 *
 * REGLA CRÍTICA: siempre inyectar RestClient.Builder (el auto-configurado
 * por Spring Boot), nunca instanciar con RestClient.create() o new RestTemplate().
 *
 * El Builder auto-configurado ya tiene registrado el ClientHttpRequestInterceptor
 * de Micrometer que:
 *   1. Lee el span activo del contexto OTel del thread actual
 *   2. Inyecta el header "traceparent" (W3C) en cada request saliente
 *      Formato: 00-{traceId}-{spanId}-{flags}
 *   3. Crea un span hijo para representar la llamada HTTP saliente
 *
 * Esto garantiza que customer-api y product-api reciban el mismo traceId
 * que originó el request en el BFF.
 */
@Configuration
@RequiredArgsConstructor
public class RestClientConfigFactory {

    // /**
    //  * Cliente para customer-api.
    //  * El Builder ya viene instrumentado — no se necesita ninguna configuración
    //  * adicional para la propagación del traceId.
    //  */
    // @Bean
    // public RestClient customerClient(
    //         RestClient.Builder builder,
    //         @Value("${clients.customer-api.base-url}") String baseUrl) {

    //     return builder
    //             .baseUrl(baseUrl)
    //             .build();
    // }

    // /**
    //  * Cliente para product-api.
    //  * Mismo principio: el Builder propaga automáticamente el contexto de tracing.
    //  */
    // @Bean
    // public RestClient productClient(
    //         RestClient.Builder builder,
    //         @Value("${clients.product-api.base-url}") String baseUrl) {

    //     return builder
    //             .baseUrl(baseUrl)
    //             .build();
    // }

    private final RestClientConfigProperties restClientConfigProperties;

    // Map of restclient builders, in case we want to add more clients.
    @Bean
    public Map<RestClientName, RestClient.Builder> restClientBuilders(JsonMapper jsonMapper, Logbook logbook, RestClient.Builder builder) {

        Map<RestClientName, RestClient.Builder> clientBuilders = new EnumMap<>(RestClientName.class);

        restClientConfigProperties.configs().forEach((clientName, config) -> {
            HttpClientSettings settings = HttpClientSettings.defaults()
                .withConnectTimeout(Duration.ofMillis(config.connectTimeout()))
                .withReadTimeout(Duration.ofMillis(config.readTimeout()));

            ClientHttpRequestFactory factory = ClientHttpRequestFactoryBuilder.httpComponents()
                .withConnectionManagerCustomizer(customizer -> 
                    customizer
                            // 5 downstreams
                        .setMaxConnTotal(500)
                            // 100 connections per downstream
                        .setMaxConnPerRoute(100))
                .build(settings);
            
            RestClient.Builder clientBuilder = builder.clone()
                    .requestFactory(factory)
                    .baseUrl(config.baseUrl())
                    .requestInterceptor(new LogbookClientHttpRequestInterceptor(logbook))
                    .configureMessageConverters(converters ->
                        converters.addCustomConverter(
                            new JacksonJsonHttpMessageConverter(jsonMapper)));
            
            clientBuilders.put(clientName, clientBuilder);
        });

        return clientBuilders;
    }

}
