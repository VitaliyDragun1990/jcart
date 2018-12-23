package com.revenat.config;

import com.revenat.jcart.core.orders.OrderService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class MockOrderServiceConfig {

    @Bean
    public OrderService orderService() {
        return Mockito.mock(OrderService.class);
    }
}
