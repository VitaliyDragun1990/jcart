package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.JCartAdminApplication;
import com.revenat.jcart.entities.Permission;
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
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {JCartAdminApplication.class})
@WebAppConfiguration
public class RoleControllerTest {

    private static final String TEST_USER = "john@gmail.com";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private RoleController roleController;

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpController() {
        roleController = context.getBean("roleController", RoleController.class);
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void returnsHeaderTitle() {
        assertThat(roleController.getHeaderTitle(), equalTo("Manage Roles"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void returnsAllPermissions() {
        List<Permission> permissions = roleController.permissionsList();

        assertThat(permissions, hasSize(9));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testListRoles() throws Exception {
        mockMvc.perform(get("/roles"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("roles"))
                .andExpect(view().name("roles/roles"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testCreateNewRole_GetForm() throws Exception {
        mockMvc.perform(get("/roles/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("role"))
                .andExpect(view().name("roles/create_role"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testCreateNewRole_PostData_OK() throws Exception {
        mockMvc.perform(post("/roles")
                .param("name", "ROLE_DUMMY")
                .param("description", "Some description"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/roles"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testCreateNewRole_PostData_ValidationError() throws Exception {
        mockMvc.perform(post("/roles")
                .param("name", "")
                .param("description", "Some description"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("role"))
                .andExpect(model().attributeHasFieldErrors("role", "name"))
                .andExpect(view().name("roles/create_role"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testEditRoleForm() throws Exception {
        mockMvc.perform(get("/roles/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("role"))
                .andExpect(view().name("roles/edit_role"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testUpdateRole_OK() throws Exception {
        mockMvc.perform(post("/roles/3")
                .param("name", "ROLE_SUPER_ADMIN")
                .param("description", "Some description"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/roles"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    public void testUpdateRole_ValidationError() throws Exception {
        mockMvc.perform(post("/roles/1")
                .param("name", "")
                .param("description", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("role"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("role", "name"))
                .andExpect(view().name("roles/edit_role"));

    }

    @Test(expected = NestedServletException.class)
    @WithUserDetails(TEST_USER)
    public void testUpdateRole_InvalidRoleId() throws Exception {
        mockMvc.perform(post("/roles/99")
                .param("name", "ROLE_DUMMY")
                .param("description", ""))
                .andDo(print());
    }
}