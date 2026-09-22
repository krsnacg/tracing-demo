package com.demo.sandbox.bff.shared.client;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface AuthApiClient {
    @GetExchange ("/basic-auth/{user}/{pass}")
    Map<String, Object> checkAuth(@PathVariable String user, @PathVariable String pass);
}