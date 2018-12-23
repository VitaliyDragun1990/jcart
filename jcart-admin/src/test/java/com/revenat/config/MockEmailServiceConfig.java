package com.revenat.config;

import com.revenat.jcart.core.common.services.EmailService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class MockEmailServiceConfig {

    @Bean
    public EmailService emailService() {
        return Mockito.mock(EmailService.class);
    }
}
