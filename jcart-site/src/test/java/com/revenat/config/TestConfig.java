package com.revenat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MockImageServiceConfig.class, MockCatalogServiceConfig.class,
        MockEmailServiceConfig.class, MockOrderServiceConfig.class,
        MockCustomerServiceConfig.class})
public class TestConfig {
}
