package com.demo.sandbox.bff.controller;

import com.demo.sandbox.bff.model.DashboardResponse;
import com.demo.sandbox.bff.service.DashboardService;
import java.util.Map;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint principal del BFF.
 *
 * Flujo de tracing para GET /dashboard/{customerId}:
 *
 *   1. Spring MVC recibe el request. Si viene con header "traceparent",
 *      OTel lo extrae y crea un span hijo. Si no viene, crea un nuevo span raíz
 *      y genera un traceId fresco (ej: desde un API Gateway sin tracing).
 *
 *   2. customerClient.getCustomer() → el RestClient inyecta "traceparent" en el
 *      request saliente hacia customer-api con el MISMO traceId.
 *
 *   3. productClient.getProducts() → ídem hacia product-api.
 *
 *   4. Los tres servicios logean con el mismo traceId en MDC → correlación total.
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    // private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    // private final CustomerClient customerClient;
    // private final ProductClient productClient;
    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable Long customerId) {

        // // El traceId ya está en MDC gracias a la auto-instrumentación del starter.
        // // Este log mostrará [traceId, spanId] en el patrón configurado en logback.
        // log.info("[BFF] Iniciando agregación de datos para customerId={}", customerId);

        // // Llamada 1: customer-api
        // // RestClient inyecta header: traceparent: 00-{traceId}-{nuevaSpanId}-01
        // // customer-api recibe ese header y continúa el mismo trace.
        // var customer = customerClient.getCustomer(customerId);
        // log.info("[BFF] Customer recibido: name={}", customer.name());

        // // Llamada 2: product-api
        // // Mismo traceId, nuevo spanId para este hop.
        // var products = productClient.getProducts();
        // log.info("[BFF] Products recibidos: count={}", products.size());

        // return new DashboardResponse(customer, products);
        return ResponseEntity.ok(dashboardService.getDashboard(customerId));
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<DashboardResponse.CustomerSummary> getCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(dashboardService.getCustomer(customerId));
    }

    @GetMapping("/customers/{customerId}/additional-info")
    public ResponseEntity<DashboardResponse.CustomerAdditionalInfo> getCustomerAdditionalInfo(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(dashboardService.getCustomerAdditionalInfo(customerId));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<DashboardResponse.ProductSummary> getProduct(
            @PathVariable Long productId) {
        return ResponseEntity.ok(dashboardService.getProduct(productId));
    }

    @GetMapping("/products")
    public ResponseEntity<List<DashboardResponse.ProductSummary>> getProducts() {
        return ResponseEntity.ok(dashboardService.getProducts());
    }

    @GetMapping("/auth/{user}/{password}")
    public ResponseEntity<Map<String, Object>> checkAuth(
            @PathVariable String user,
            @PathVariable String password) {
        return ResponseEntity.ok(dashboardService.checkAuth(user, password));
    }

    @GetMapping("/slow/{seconds}")
    public ResponseEntity<Map<String, Object>> getDelayedResponse(@PathVariable int seconds) {
        return ResponseEntity.ok(dashboardService.getDelayedResponse(seconds));
    }

    @GetMapping("/error/{statusCode}")
    public ResponseEntity<Void> triggerError(@PathVariable int statusCode) {
        dashboardService.triggerError(statusCode);
        return ResponseEntity.noContent().build();
    }
}
