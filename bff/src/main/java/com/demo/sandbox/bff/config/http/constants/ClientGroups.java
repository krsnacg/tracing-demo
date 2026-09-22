package com.demo.sandbox.bff.config.http.constants;

import lombok.experimental.UtilityClass;

@UtilityClass 
public class ClientGroups {

    public final String CUSTOMER_API = "customer-api";
    public final String PRODUCT_API = "product-api";

    // External services
    public final String AUTH_API = "auth-api";
    public final String ERROR_API = "error-api";
    public final String SLOW_API = "slow-api";

}