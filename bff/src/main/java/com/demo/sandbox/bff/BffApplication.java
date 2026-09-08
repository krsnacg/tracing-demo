package com.demo.sandbox.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.demo.sandbox.bff.config.properties.RestClientConfigProperties;

@EnableConfigurationProperties(RestClientConfigProperties.class)
@SpringBootApplication
public class BffApplication {

    public static void main(String[] args) {
        SpringApplication.run(BffApplication.class, args);
    }
}
