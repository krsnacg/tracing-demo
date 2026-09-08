package com.demo.product.controller;

import com.demo.product.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller del microservicio de productos.
 *
 * Mismo mecanismo que customer-api: el starter OTel extrae el header
 * traceparent del request entrante y continúa el trace iniciado en el BFF.
 *
 * Este servicio logeará con el MISMO traceId que:
 *   - El BFF (que originó el trace)
 *   - Customer-api (que también recibió el mismo traceparent)
 *
 * Esto permite en Jaeger/Tempo ver los 3 spans bajo un único trace:
 *   trace: abc123...
 *     └── [bff] GET /dashboard/1           200ms
 *           ├── [customer-api] GET /customers/1    30ms
 *           └── [product-api]  GET /products       45ms
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private static final Map<Long, Product> PRODUCTS = Map.of(
            1L, new Product(1L, "Laptop Pro 15",   1299.99, "ELECTRONICS"),
            2L, new Product(2L, "Wireless Mouse",    49.99, "ACCESSORIES"),
            3L, new Product(3L, "Mechanical Keyboard", 159.99, "ACCESSORIES"),
            4L, new Product(4L, "4K Monitor",        599.99, "ELECTRONICS"),
            5L, new Product(5L, "USB-C Hub",          39.99, "ACCESSORIES")
    );

    @GetMapping
    public List<Product> getProducts(
            @RequestHeader(value = "traceparent", required = false) String traceparent) {

        log.info("[PRODUCT-API] Listando productos. traceparent={}", traceparent);

        var products = List.copyOf(PRODUCTS.values());
        log.info("[PRODUCT-API] Retornando {} productos", products.size());
        return products;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(
            @PathVariable Long id,
            @RequestHeader(value = "traceparent", required = false) String traceparent) {

        log.info("[PRODUCT-API] Request producto. productId={} traceparent={}", id, traceparent);

        var product = PRODUCTS.get(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }
}
