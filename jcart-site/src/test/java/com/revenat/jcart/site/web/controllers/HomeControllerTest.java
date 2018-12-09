package com.revenat.jcart.site.web.controllers;

import com.revenat.config.MockImageServiceConfig;
import com.revenat.jcart.JCartSiteApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({JCartSiteApplication.class, MockImageServiceConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class HomeControllerTest {

    private static final String TEST_USER = "anna@gmail.com";

    @Autowired
    private WebApplicationContext context;

    private HomeController controller;

    private MockMvc mockMvc;

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpController() {
        controller = context.getBean("homeController", HomeController.class);
    }

    @Test
    public void getTitle() {
        String headerTitle = controller.getHeaderTitle();

        assertThat(headerTitle, equalTo("Home"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testGetHomePage() throws Exception {
        mockMvc.perform(get("/home"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("headerTitle", "authenticatedUser", "categories"))
                .andExpect(model().attribute("categories", hasSize(3)))
                .andExpect(view().name("public/home"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testGetCategory() throws Exception {
        mockMvc.perform(get("/categories/Flowers"))
                .andDo(print())
                .andExpect(model().attributeExists("category"))
                .andExpect(model().attribute("category", hasProperty("id", equalTo(1))))
                .andExpect(view().name("public/category"));
    }
}