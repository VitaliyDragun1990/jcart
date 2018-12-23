package com.revenat.jcart.site.web.controllers;

import com.revenat.config.TestConfig;
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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
        classes = {JCartSiteApplication.class, TestConfig.class}
)
@WebAppConfiguration
@ActiveProfiles("test")
public class CheckoutControllerTest {

    private static final String AUTH_CUSTOMER = "john@gmail.com";
    private static final String HEADER_TITLE = "Checkout";
    private static final String VIEW_CHECKOUT = "auth/checkout";

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private CheckoutController controller;

    private MockMvc mvc;

    @Before
    public void setUpMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithUserDetails(AUTH_CUSTOMER)
    public void getHeaderTitle_ShouldReturnHeaderTitle() {
        assertThat(controller.getHeaderTitle(), equalTo(HEADER_TITLE));
    }

    @Test
    @WithUserDetails(AUTH_CUSTOMER)
    public void checkout_ShouldAddOrderEntryToModelAndRenderCheckoutView() throws Exception {
        mvc.perform(get("/checkout"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cart", "order"))
                .andExpect(view().name(VIEW_CHECKOUT));
    }
}