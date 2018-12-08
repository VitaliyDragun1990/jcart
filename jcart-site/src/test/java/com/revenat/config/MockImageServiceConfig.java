package com.revenat.config;

import com.revenat.jcart.core.common.services.ImageService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class MockImageServiceConfig {

    @Bean
    public ImageService imageService() {
        return Mockito.mock(ImageService.class);
    }
}
