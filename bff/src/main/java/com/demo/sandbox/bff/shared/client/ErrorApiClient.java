package com.demo.sandbox.bff.shared.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface ErrorApiClient {
    @GetExchange ("/status/{code}")
    Void triggerStatus(@PathVariable int code);
}
