package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.JCartAdminApplication;
import com.revenat.jcart.admin.security.AuthenticatedUser;
import com.revenat.jcart.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(JCartAdminApplication.class)
@WebAppConfiguration
public class HomeControllerIntegrationIT {

    private AuthenticatedUser dummyAuthentication;


    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private HomeController controller;

    @Before
    public void setUpMockMvc() throws Exception {
        controller = new HomeController();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
//                .standaloneSetup(controller)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void createDummyUser() {
        User dummyUser = new User();
        dummyUser.setName("Jack");
        dummyUser.setEmail("dummy@gmail.com");
        dummyUser.setPassword("password");
        dummyAuthentication = new AuthenticatedUser(dummyUser);
    }

    @Test
    @WithUserDetails("john@gmail.com")
    public void testHome() throws Exception {
        mockMvc.perform(get("/home")
//                .with(user(dummyAuthentication))
        )
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("authenticatedUser"));
    }
}