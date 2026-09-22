package com.demo.sandbox.bff.shared.client;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface SlowApiClient {
    @GetExchange ("/delay/{seconds}")
    Map<String, Object> delayed(@PathVariable int seconds);
}