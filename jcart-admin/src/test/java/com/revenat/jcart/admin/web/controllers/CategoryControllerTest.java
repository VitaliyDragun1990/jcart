package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.JCartAdminApplication;
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
import org.springframework.web.util.NestedServletException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JCartAdminApplication.class)
@WebAppConfiguration
public class CategoryControllerTest {

    private static final String TEST_USER = "john@gmail.com";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private CategoryController controller;

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpController() {
        controller = context.getBean("categoryController", CategoryController.class);
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void returnsHeaderTitle() {
        assertThat(controller.getHeaderTitle(), equalTo("Manage Categories"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testListCategories() throws Exception {
        mockMvc.perform(get("/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("categories"))
                .andExpect(view().name("categories/categories"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testGetCategoryForm() throws Exception {
        mockMvc.perform(get("/categories/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("category"))
                .andExpect(view().name("categories/create_category"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testCreateNewCategory_OK() throws Exception {
        mockMvc.perform(post("/categories")
                .param("name", "test").param("description", "test")
                .param("displayOrder", "1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/categories"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testCreateNewCategory_FailValidationError() throws Exception {
        mockMvc.perform(post("/categories")
                .param("name", "")
                .param("description", "")
                .param("displayOrder", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("category"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("category", "name"))
                .andExpect(view().name("categories/create_category"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testGetEditCategoryForm() throws Exception {
        mockMvc.perform(get("/categories/1"))
                .andDo(print())
                .andExpect(model().attributeExists("category"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/edit_category"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testUpdateCategory_OK() throws Exception {
        mockMvc.perform(post("/categories/1")
                .param("name", "test")
                .param("description", "")
                .param("displayOrder", ""))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/categories"));
    }

    @Test(expected = NestedServletException.class)
    @WithUserDetails(TEST_USER)
    public void testUpdateRole_InvalidRoleId() throws Exception {
        mockMvc.perform(post("/categories/99")
                .param("name", "test")
                .param("description", "")
                .param("displayOrder", ""))
                .andDo(print());
    }
}