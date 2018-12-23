package com.revenat.config;

import com.revenat.builders.CustomerBuilder;
import com.revenat.jcart.core.customers.CustomerService;
import com.revenat.jcart.core.entities.Customer;

import static org.mockito.Mockito.when;

public final class SecurityConfigurer {

    private static final String CUSTOMER_PASSWORD = "password";

    public static void configureAuthenticatedCustomer(CustomerService customerService, String customerEmail) {
        when(customerService.getCustomerByEmail(customerEmail)).thenReturn(createAuthenticatedCustomerWithEmail(customerEmail));
    }

    private static Customer createAuthenticatedCustomerWithEmail(String customerEmail) {
        return CustomerBuilder.getBuilder()
                .withEmail(customerEmail)
                .withPassword(CUSTOMER_PASSWORD)
                .build();
    }
}
