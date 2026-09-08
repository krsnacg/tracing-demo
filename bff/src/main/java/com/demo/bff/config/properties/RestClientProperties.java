package com.demo.bff.config.properties;

public record RestClientProperties(
    String baseUrl,
    Integer connectTimeout,
    Integer readTimeout
) {
    
}
