package com.demo.sandbox.bff.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.stereotype.Service;

import com.demo.sandbox.bff.model.DashboardResponse;
import com.demo.sandbox.bff.shared.client.AuthApiClient;
import com.demo.sandbox.bff.shared.client.ErrorApiClient;
import com.demo.sandbox.bff.shared.client.ProductClient;
import com.demo.sandbox.bff.shared.client.SlowApiClient;
import com.demo.sandbox.bff.shared.client.customer.CustomerAdditionalInfoClient;
import com.demo.sandbox.bff.shared.client.customer.CustomerClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CustomerClient customerClient;
    private final CustomerAdditionalInfoClient additionalInfoClient;
    private final ProductClient productClient;
    private final AuthApiClient authApiClient;
    private final ErrorApiClient errorApiClient;
    private final SlowApiClient slowApiClient;
    private final Executor taskExecutor; // el AsyncTaskExecutor de Spring, ya decorado

    public DashboardResponse getDashboard(Long customerId) {
        log.info("Iniciando agregación paralela para customerId={}", customerId);

        var customerFuture = CompletableFuture.supplyAsync(
                () -> customerClient.getCustomer(customerId), taskExecutor);

        var additionalInfoFuture = CompletableFuture.supplyAsync(
            () -> additionalInfoClient.getAdditionalInfo(customerId), taskExecutor);

        var productsFuture = CompletableFuture.supplyAsync(
                () -> productClient.getProducts(), taskExecutor);

        var customer = customerFuture.join();
        var additionalInfo = additionalInfoFuture.join();
        var products = productsFuture.join();

        log.info("Agregación completa. customer={} products={}", customer.name(), products.size());

        return new DashboardResponse(customer, additionalInfo, products);
    }

    public DashboardResponse.CustomerSummary getCustomer(Long customerId) {
        return customerClient.getCustomer(customerId);
    }

    public DashboardResponse.CustomerAdditionalInfo getCustomerAdditionalInfo(Long customerId) {
        return additionalInfoClient.getAdditionalInfo(customerId);
    }

    public DashboardResponse.ProductSummary getProduct(Long productId) {
        return productClient.getProduct(productId);
    }

    public List<DashboardResponse.ProductSummary> getProducts() {
        return productClient.getProducts();
    }

    public Map<String, Object> checkAuth(String user, String password) {
        return authApiClient.checkAuth(user, password);
    }

    public void triggerError(int statusCode) {
        errorApiClient.triggerStatus(statusCode);
    }

    public Map<String, Object> getDelayedResponse(int seconds) {
        return slowApiClient.delayed(seconds);
    }
}
