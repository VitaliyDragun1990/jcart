package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.JCartAdminApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(JCartAdminApplication.class)
@WebAppConfiguration
public class AuthenticationMechanismIT {

    private static final String AUTH_USER_EMAIL = "john@gmail.com";
    private static final String AUTH_USER_PASSWORD = "green";

    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void formLogin_InvalidCredentials_UnauthenticatedResponse() throws Exception {
        mockMvc.perform(formLogin().user("invalid").password("invalid"))
                .andExpect(unauthenticated());
    }

    @Test
    public void formLogin_ValidCredentials_Authenticatedresponse() throws Exception {
        mockMvc.perform(formLogin().user(AUTH_USER_EMAIL).password(AUTH_USER_PASSWORD))
                .andExpect(authenticated());
    }
}
