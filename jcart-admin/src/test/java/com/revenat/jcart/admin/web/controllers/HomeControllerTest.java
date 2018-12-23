package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.JCartAdminApplication;
import com.revenat.jcart.admin.security.AuthenticatedUser;
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

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(JCartAdminApplication.class)
@WebAppConfiguration
public class HomeControllerTest {
    private static final String AUTH_USER_EMAIL = "john@gmail.com";

    private static final String HEADER_TITLE = "Home";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private HomeController controller;

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void getHeaderTitle_ShouldReturnCorrectHeaderTitle() {
        assertThat(controller.getHeaderTitle(), equalTo(HEADER_TITLE));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void home_ShouldRenderHomeView() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("authenticatedUser"))
                .andExpect(view().name("home"));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void getCurrentUser_ShouldReturnInstanceOfCurrentlyLoggedInUser() {
        AuthenticatedUser authenticatedUser = HomeController.getCurrentUser();
        assertNotNull("AuthenticatedUser can not be null.", authenticatedUser);

        String username = authenticatedUser.getUsername();
        assertNotNull("Authenticated user's username can not be null.", username);
        assertThat(username, equalTo(AUTH_USER_EMAIL));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void isLoggedIn_ShouldReturnTrue() {
        boolean isLoggedIn = HomeController.isLoggedIn();

        assertTrue(isLoggedIn);
    }

}