package com.demo.sandbox.bff.config.properties;

public record RestClientProperties(
    String baseUrl,
    Integer connectTimeout,
    Integer readTimeout
) {
    
}
