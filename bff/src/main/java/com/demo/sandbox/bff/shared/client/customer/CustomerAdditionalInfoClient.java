package com.demo.sandbox.bff.shared.client.customer;

import com.demo.sandbox.bff.model.DashboardResponse.CustomerAdditionalInfo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface CustomerAdditionalInfoClient {

    @GetExchange("/customers/{id}/additional-info")
    CustomerAdditionalInfo getAdditionalInfo(@PathVariable Long id);
}
