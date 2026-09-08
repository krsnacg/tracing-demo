package com.demo.bff.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.stereotype.Service;

import com.demo.bff.client.CustomerClient;
import com.demo.bff.client.ProductClient;
import com.demo.bff.model.DashboardResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final Executor taskExecutor; // el AsyncTaskExecutor de Spring, ya decorado

    public DashboardResponse getDashboard(Long customerId) {
        log.info("Iniciando agregación paralela para customerId={}", customerId);

        var customerFuture = CompletableFuture.supplyAsync(
                () -> customerClient.getCustomer(customerId), taskExecutor);

        var productsFuture = CompletableFuture.supplyAsync(
                () -> productClient.getProducts(), taskExecutor);

        var customer = customerFuture.join();
        var products = productsFuture.join();

        log.info("Agregación completa. customer={} products={}", customer.name(), products.size());

        return new DashboardResponse(customer, products);
    }
}
