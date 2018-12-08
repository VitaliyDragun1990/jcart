package com.revenat.jcart.site.web.controllers;

import com.revenat.config.MockImageServiceConfig;
import com.revenat.jcart.JCartSiteApplication;
import com.revenat.jcart.core.common.services.ImageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
public class ProductControllerTest {
    private static final String TEST_USER = "anna@gmail.com";

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ProductController controller;

    @Autowired
    private ImageService mockImageService;

    private MockMvc mockMvc;

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpMocks() {
        Mockito.reset(mockImageService);
    }

    @Test
    public void getTitle() {
        String headerTitle = controller.getHeaderTitle();

        assertThat(headerTitle, equalTo("Product"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void searchProducts_NoResults() throws Exception {
        mockMvc.perform(get("/products")
                .param("q", "DummyProduct"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("authenticatedUser", "headerTitle", "products"))
                .andExpect(model().attribute("products", hasSize(0)))
                .andExpect(view().name("public/products"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void searchProducts_Positive() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/products")
                .param("q", "Toy"))
                .andDo(print())
                .andExpect(model().attributeExists("products", "headerTitle", "authenticatedUser"))
                .andExpect(model().attribute("products", hasSize(5)))
                .andExpect(model().attribute("headerTitle", equalTo("Product")))
                .andExpect(view().name("public/products"))
                .andReturn().getResponse();

        assertThat(response.getStatus(), equalTo(HttpStatus.OK.value()));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void getProductPage_Positive() throws Exception {
        mockMvc.perform(get("/products/P1001"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("product"))
                .andExpect(view().name("public/product"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void showProductImage() throws Exception {
        when(mockImageService.loadImage(anyString())).thenReturn(new byte[] {1, 2, 3});

        mockMvc.perform(get("/products/images/1"))
                .andDo(print());

        verify(mockImageService, times(1)).loadImage(anyString());
    }
}