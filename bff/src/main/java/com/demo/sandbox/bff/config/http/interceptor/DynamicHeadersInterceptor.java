package com.demo.sandbox.bff.config.http.interceptor;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.demo.sandbox.bff.config.http.constants.HeaderConstants;


@Component
public class DynamicHeadersInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        
        request.getHeaders().set(
                HeaderConstants.X_REQUEST_CONTEXT,
                UUID.randomUUID() + ";created-at=" + Instant.now());

        return execution.execute(request, body);
    }
}
