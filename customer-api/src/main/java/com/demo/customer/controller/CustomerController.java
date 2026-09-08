package com.demo.customer.controller;

import com.demo.customer.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller del microservicio de clientes.
 *
 * Cuando el BFF llama a este endpoint, el request llega con el header:
 *   <div>traceparent: 00-{traceId}-{spanIdDelBFF}-01</div>
 *
 * El starter OTel de Spring Boot intercepta el request HTTP entrante
 * (via ServerHttpObservationFilter), extrae el traceId del header traceparent
 * y crea un nuevo span hijo bajo ese mismo traceId.
 *
 * Resultado: %X{traceId} en los logs de este servicio es IDÉNTICO
 * al %X{traceId} de los logs del BFF.
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    // Datos en memoria para la demo — evita dependencia de base de datos
    private static final Map<Long, Customer> CUSTOMERS = Map.of(
            1L, new Customer(1L, "Gabriel Torres", "gabriel@demo.com", "PREMIUM"),
            2L, new Customer(2L, "Ana García",    "ana@demo.com",     "VIP"),
            3L, new Customer(3L, "Luis Mendoza",   "luis@demo.com",    "STANDARD")
    );

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(
            @PathVariable Long id,
            // Recibimos el header traceparent para mostrarlo en logs (solo didáctico)
            @RequestHeader(value = "traceparent", required = false) String traceparent) {

        // Este log mostrará el MISMO traceId que el BFF — la propagación funcionó.
        log.info("[CUSTOMER-API] Request recibido. customerId={} traceparent={}",
                id, traceparent);

        var customer = CUSTOMERS.get(id);
        if (customer == null) {
            log.warn("[CUSTOMER-API] Customer no encontrado. customerId={}", id);
            return ResponseEntity.notFound().build();
        }

        log.info("[CUSTOMER-API] Respondiendo customer. name={} tier={}",
                customer.name(), customer.tier());

        return ResponseEntity.ok(customer);
    }
}
