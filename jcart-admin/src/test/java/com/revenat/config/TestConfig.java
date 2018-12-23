package com.revenat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MockCatalogServiceConfig.class, MockOrderServiceConfig.class,
        MockImageServiceConfig.class, MockEmailServiceConfig.class, MockCustomerServiceConfig.class})
public class TestConfig {
}
