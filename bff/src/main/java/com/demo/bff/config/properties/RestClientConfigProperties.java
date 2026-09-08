package com.demo.bff.config.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rest-client")
public record RestClientConfigProperties(
    Map<RestClientName, RestClientProperties> configs
) {}
