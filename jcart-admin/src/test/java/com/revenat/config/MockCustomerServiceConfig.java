package com.revenat.config;

import com.revenat.jcart.core.customers.CustomerService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class MockCustomerServiceConfig {

    @Bean
    public CustomerService customerService() {
        return Mockito.mock(CustomerService.class);
    }
}
