package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.JCartAdminApplication;
import com.revenat.config.MockImageServiceConfig;
import com.revenat.jcart.core.common.services.ImageService;
import com.revenat.jcart.core.entities.Category;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {JCartAdminApplication.class, MockImageServiceConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class ProductControllerTest {

    private static final String TEST_USER = "john@gmail.com";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private ProductController controller;

    @Autowired
    private ImageService mockImageService;

    @Before
    public void setUpMocks() {
        Mockito.reset(mockImageService);
    }

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpController() {
        controller = context.getBean("productController", ProductController.class);
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testReturnsHeaderTitle() {
        String headerTitle = controller.getHeaderTitle();

        assertThat(headerTitle, equalTo("Manage Products"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testReturnsAllCategories() {
        List<Category> allCategories = controller.categoriesList();

        assertThat(allCategories, hasSize(3));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testListAllProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("products", hasSize(greaterThan(4))))
                .andExpect(view().name("products/products"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testGetProductForm() throws Exception {
        mockMvc.perform(get("/products/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("product"))
                .andExpect(view().name("products/create_product"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testCreateNewProduct_OK() throws Exception {
        mockMvc.perform(post("/products")
                .param("sku", "15488")
                .param("name", "dummy")
                .param("price", "1.0")
                .param("categoryId", "1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testCreateNewProduct_Fail_ValidationError() throws Exception {
        mockMvc.perform(post("/products")
                .param("name", "dummy")
                .param("price", "1.0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("product"))
                .andExpect(model().hasErrors()).andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrors("product", "sku", "categoryId"))
                .andExpect(view().name("products/create_product"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testGetEditProductForm() throws Exception {
        mockMvc.perform(get("/products/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("product"))
                .andExpect(view().name("products/edit_product"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testGet404WhenProductNotFound() throws Exception {
        mockMvc.perform(get("/products/25"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists("exception"))
                .andExpect(view().name("error/404"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testShowProductImage() throws Exception {
        mockMvc.perform(get("/products/images/1"))
                .andDo(print());

        verify(mockImageService, times(1)).loadImage("1.jpg");
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testUpdateProduct_OK() throws Exception {
        mockMvc.perform(post("/products/1")
                .param("sku", "15488")
                .param("name", "dummy")
                .param("price", "1.0")
                .param("categoryId", "1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testUpdateProduct_FailValidationError() throws Exception {
        mockMvc.perform(post("/products/1")
                .param("id", "1")
                .param("sku", "15488")
                .param("name", "dummy")
                .param("categoryId", "1")
                .param("price", "0.0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("product"))
                .andExpect(model().hasErrors()).andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("product", "price"))
                .andExpect(view().name("products/edit_product"));
    }
}