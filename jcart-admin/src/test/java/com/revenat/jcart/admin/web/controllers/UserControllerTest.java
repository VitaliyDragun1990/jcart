package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.JCartAdminApplication;
import com.revenat.jcart.entities.Role;
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

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {JCartAdminApplication.class})
@WebAppConfiguration
public class UserControllerTest {

    private static final String TEST_USER = "john@gmail.com";
    private static final String DUMMY_NAME = "dummy";
    private static final String DUMMY_EMAIL = "dummy@gmail.com";
    private static final String DUMMY_PASS = "test";


    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private UserController controller;

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpController() {
        controller = context.getBean("userController", UserController.class);
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void returnsHeaderTitle() {
        assertThat(controller.getHeaderTitle(), equalTo("Manage Users"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void returnsAllRoles() {
        List<Role> allRoles = controller.rolesList();

        assertThat(allRoles, hasSize(4));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testListUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("users/users"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testCreateNewUser_GetForm() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("users/create_user"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testCreateNewUser_PostData_OK() throws Exception {
        mockMvc.perform(post("/users")
                .param("name", DUMMY_NAME)
                .param("email", DUMMY_EMAIL)
                .param("password", DUMMY_PASS))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testCreateNewUser_PostData_ValidationError() throws Exception {
        mockMvc.perform(post("/users")
                .param("name", "")
                .param("email", "")
                .param("password", DUMMY_PASS))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrors("user", "name", "email"))
                .andExpect(view().name("users/create_user"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testEditUserForm() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("users/edit_user"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testUpdateUser_OK() throws Exception {
        mockMvc.perform(post("/users/2")
                .param("name", DUMMY_NAME)
                .param("email", DUMMY_EMAIL)
                .param("password", DUMMY_PASS))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testUpdateUser_ValidationError() throws Exception {
        mockMvc.perform(post("/users/2")
                .param("name", DUMMY_NAME)
                .param("email", "")
                .param("password", DUMMY_PASS))
                .andDo(print())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("user", "email"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/edit_user"));
    }

    @Test(expected = NestedServletException.class)
    @WithUserDetails(TEST_USER)
    public void testUpdateUser_InvalidUserId() throws Exception {
        mockMvc.perform(post("/users/99")
                .param("name", DUMMY_NAME)
                .param("email", DUMMY_EMAIL)
                .param("password", DUMMY_PASS))
                .andDo(print());
    }
}