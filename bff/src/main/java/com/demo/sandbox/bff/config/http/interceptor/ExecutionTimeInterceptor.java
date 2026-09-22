package com.demo.sandbox.bff.config.http.interceptor;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ExecutionTimeInterceptor implements ClientHttpRequestInterceptor {

    private String groupName;
    
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        
        long start = System.currentTimeMillis();
        try {
            return execution.execute(request, body);
        } finally {
            log.info("{} took {} ms", groupName, System.currentTimeMillis() - start);
        }
    }
}
