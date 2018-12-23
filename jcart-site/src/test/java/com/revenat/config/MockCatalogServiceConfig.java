package com.revenat.config;

import com.revenat.jcart.core.catalog.CatalogService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class MockCatalogServiceConfig {

    @Bean
    public CatalogService catalogService() {
        return Mockito.mock(CatalogService.class);
    }
}
