package com.revenat.config;

import com.revenat.jcart.core.customers.CustomerService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class MockCustomerServiceConfig {

    private static final String AUTHENTICATED_CUSTOMER_EMAIL = "john@gmail.com";

    @Bean
    public CustomerService customerService() {
        CustomerService customerService = Mockito.mock(CustomerService.class);

        SecurityConfigurer.configureAuthenticatedCustomer(customerService, AUTHENTICATED_CUSTOMER_EMAIL);

        return customerService;
    }
}
