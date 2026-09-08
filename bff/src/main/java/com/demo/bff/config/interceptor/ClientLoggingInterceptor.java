package com.demo.bff.config.interceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        log.info(">>> {} {}", request.getMethod(), request.getURI());
        log.info(">>> Headers: {}", request.getHeaders());
        if (body.length > 0) {
            log.info(">>> Body: {}", new String(body, StandardCharsets.UTF_8));
        }

        ClientHttpResponse response = execution.execute(request, body);

        log.info("<<< Headers: {}", response.getHeaders());
        // Envolver para poder leer el body múltiples veces
        byte[] responseBody = response.getBody().readAllBytes();
        log.info("<<< Status: {}", response.getStatusCode());
        log.info("<<< Body: {}", new String(responseBody, StandardCharsets.UTF_8));

        // Devolver una copia con el body intacto
        return new BufferingClientHttpResponseWrapper(response, responseBody);
    }
    
}
