package com.revenat.config;

import com.revenat.jcart.core.common.services.ImageService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;;

@Configuration
public class MockImageServiceConfig {

    @Bean
    @Profile("test")
    public ImageService imageService() {
        return Mockito.mock(ImageService.class);
    }
}
