package com.revenat.jcart.admin.config;

import com.revenat.jcart.common.services.EmailService;
import com.revenat.jcart.security.SecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

@Configuration
@Profile("test")
public class MockTemplateConfig {

    @Bean
    public SecurityService securityService() {
        return mock(SecurityService.class);
    }

    @Bean
    public EmailService emailService() {
        return mock(EmailService.class);
    }
}
