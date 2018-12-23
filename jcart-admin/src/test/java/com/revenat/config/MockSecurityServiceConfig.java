package com.revenat.config;

import com.revenat.jcart.core.security.SecurityService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class MockSecurityServiceConfig {
    private static final String USER_WITH_AUTHORITIES_EMAIL = "john@gmail.com";
    private static final String USER_WITHOUT_AUTHORITIES_EMAIL = "anna@gmail.com";

    @Bean
    public SecurityService securityService() {
        SecurityService securityService = Mockito.mock(SecurityService.class);

        SecurityConfigurer.configureUserWithFullAuthorities(securityService, USER_WITH_AUTHORITIES_EMAIL);
        SecurityConfigurer.configureUserWithoutAuthorities(securityService, USER_WITHOUT_AUTHORITIES_EMAIL);

        return securityService;
    }
}
