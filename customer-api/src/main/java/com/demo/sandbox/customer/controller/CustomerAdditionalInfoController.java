package com.demo.sandbox.customer.controller;

import com.demo.sandbox.customer.model.CustomerAdditionalInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/customers")
public class CustomerAdditionalInfoController {

    private static final Logger log = LoggerFactory.getLogger(CustomerAdditionalInfoController.class);

    private static final Map<Long, CustomerAdditionalInfo> ADDITIONAL_INFO = Map.of(
            1L, new CustomerAdditionalInfo(1250, "ACTIVE"),
            2L, new CustomerAdditionalInfo(840, "ACTIVE"),
            3L, new CustomerAdditionalInfo(120, "PENDING")
    );

    @GetMapping("/{id}/additional-info")
    public ResponseEntity<CustomerAdditionalInfo> getAdditionalInfo(
            @PathVariable Long id,
            @RequestHeader(value = "traceparent", required = false) String traceparent,
            @RequestHeader(value = "X-Request-Context", required = false) String requestContext) {

        log.info("[CUSTOMER-API] Additional info request recibido. customerId={} traceparent={} requestContext={}",
                id, traceparent, requestContext);

        var additionalInfo = ADDITIONAL_INFO.get(id);
        if (additionalInfo == null) {
            log.warn("[CUSTOMER-API] Additional info no encontrada. customerId={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(additionalInfo);
    }
}
