package com.revenat.jcart.admin.web.controllers;

import com.revenat.config.MockSecurityServiceConfig;
import com.revenat.config.SecurityConfigurer;
import com.revenat.config.TestConfig;
import com.revenat.jcart.JCartAdminApplication;
import com.revenat.jcart.admin.web.controllers.builders.PermissionBuilder;
import com.revenat.jcart.admin.web.controllers.builders.RoleBuilder;
import com.revenat.jcart.core.entities.Permission;
import com.revenat.jcart.core.entities.Role;
import com.revenat.jcart.core.exceptions.JCartException;
import com.revenat.jcart.core.security.SecurityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
        classes = {JCartAdminApplication.class, TestConfig.class, MockSecurityServiceConfig.class}
)
@WebAppConfiguration
@ActiveProfiles("test")
public class RoleControllerTest {

    private static final String AUTH_USER_EMAIL = "john@gmail.com";

    private static final String HEADER_TITLE = "Manage Roles";
    private static final Integer ROLE_ID = 1;
    private static final String ROLE_NAME = "Role name";
    private static final String ROLE_DESCRIPTION = "Role description";
    private static final String VIEW_ROLES = "roles/roles";
    private static final String VIEW_CREATE_ROLE = "roles/create_role";
    private static final String VIEW_EDIT_ROLE = "roles/edit_role";
    private static final String VIEW_404 = "error/404";
    private static final String VIEW_500 = "error/500";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private SecurityService securityService;

    private MockMvc mockMvc;
    private RoleController roleController;

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpAuthenticatedUser() {
        Mockito.reset(securityService);
        SecurityConfigurer.configureUserWithFullAuthorities(securityService, AUTH_USER_EMAIL);
    }

    @Before
    public void setUpController() {
        roleController = context.getBean("roleController", RoleController.class);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void getHeaderTitle_ReturnsHeaderTitle() {
        assertThat(roleController.getHeaderTitle(), equalTo(HEADER_TITLE));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void permissionsList_ShouldReturnListWithAllPermissionEntries() {
        Permission permission = PermissionBuilder.getBuilder().build();
        when(securityService.getAllPermissions()).thenReturn(Arrays.asList(permission));

        List<Permission> permissions = roleController.permissionsList();

        verify(securityService, times(1)).getAllPermissions();
        assertThat(permissions, hasSize(1));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void listRoles_ShouldAddRoleEntriesToModelAndRenderRolesView() throws Exception {
        Role role = RoleBuilder.getBuilder()
                .withId(ROLE_ID)
                .withName(ROLE_NAME)
                .withDescription(ROLE_DESCRIPTION)
                .build();

        when(securityService.getAllRoles()).thenReturn(Arrays.asList(role));

        mockMvc.perform(get("/roles"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attribute("roles", hasItem(
                        allOf(
                                hasProperty("id", is(ROLE_ID)),
                                hasProperty("name", is(ROLE_NAME)),
                                hasProperty("description", is(ROLE_DESCRIPTION))
                        )
                )))
                .andExpect(model().attribute("roles", hasSize(1)))
                .andExpect(view().name(VIEW_ROLES));

        verify(securityService, times(1)).getAllRoles();
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void createRoleForm_ShouldAddRoleEntryToModelAndRenderCreateRoleView() throws Exception {
        mockMvc.perform(get("/roles/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("role"))
                .andExpect(view().name(VIEW_CREATE_ROLE));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void crateRole_NewRoleEntry_ShouldCreateRoleAndRendersRolesView() throws Exception {
        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        when(securityService.createRole(Matchers.isA(Role.class))).thenAnswer(
                (Answer<Role>) invocationOnMock -> (Role) invocationOnMock.getArguments()[0]);
        when(securityService.getRoleByName(anyString())).thenReturn(null);

        mockMvc.perform(post("/roles")
                .param("name", ROLE_NAME)
                .param("description", ROLE_DESCRIPTION))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/roles"));

        verify(securityService, times(1)).createRole(roleCaptor.capture());
        verify(securityService, times(1)).getRoleByName(ROLE_NAME);
        Role createdRole = roleCaptor.getValue();
        assertThat(createdRole.getName(), equalTo(ROLE_NAME));
        assertThat(createdRole.getDescription(), equalTo(ROLE_DESCRIPTION));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void createRole_NameAlreadyTaken_ShouldRenderCreateRoleViewAndReturnValidationErrors() throws Exception {
        when(securityService.getRoleByName(anyString())).thenReturn(RoleBuilder.getBuilder().build());

        mockMvc.perform(post("/roles")
                .param("name", ROLE_NAME)
                .param("description", ROLE_DESCRIPTION))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("role"))
                .andExpect(model().attributeHasFieldErrors("role", "name"))
                .andExpect(model().errorCount(1))
                .andExpect(view().name(VIEW_CREATE_ROLE));

        verify(securityService, times(1)).getRoleByName(ROLE_NAME);
        verify(securityService, times(0)).createRole(Matchers.isA(Role.class));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void editRoleForm_RoleFound_ShouldAddRoleEntryToModelAndRenderEditRoleView() throws Exception {
        Role role = RoleBuilder.getBuilder()
                .withId(ROLE_ID)
                .withName(ROLE_NAME)
                .withDescription(ROLE_DESCRIPTION)
                .build();
        when(securityService.getRoleById(ROLE_ID)).thenReturn(role);

        mockMvc.perform(get("/roles/"+ROLE_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("role"))
                .andExpect(model().attribute("role",
                        allOf(
                                hasProperty("id", is(ROLE_ID)),
                                hasProperty("name", is(ROLE_NAME)),
                                hasProperty("description", is(ROLE_DESCRIPTION)),
                                hasProperty("permissions", hasSize(0))
                        )))
                .andExpect(view().name(VIEW_EDIT_ROLE));

        verify(securityService, times(1)).getRoleById(ROLE_ID);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void editRoleForm_RoleNotFound_ShouldAddExceptionToModelAndRender404View() throws Exception {
        when(securityService.getRoleById(ROLE_ID)).thenReturn(null);

        mockMvc.perform(get("/roles/"+ROLE_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists("exception"))
                .andExpect(view().name(VIEW_404));

        verify(securityService, times(1)).getRoleById(ROLE_ID);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void updateRole_UpdatedRoleEntry_ShouldUpdateRoleAndRenderRolesView() throws Exception {
        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        when(securityService.updateRole(Matchers.isA(Role.class))).thenAnswer(
                (Answer<Role>) invocationOnMock -> (Role) invocationOnMock.getArguments()[0]);

        mockMvc.perform(post("/roles/"+ROLE_ID)
                .param("name", ROLE_NAME)
                .param("description", ROLE_DESCRIPTION))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/roles"));

        verify(securityService, times(1)).updateRole(roleCaptor.capture());
        Role updatedRole = roleCaptor.getValue();
        assertThat(updatedRole.getName(), equalTo(ROLE_NAME));
        assertThat(updatedRole.getDescription(), equalTo(ROLE_DESCRIPTION));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void updateRole_NameIsEmpty_ShouldRenderEditRoleViewAndReturnValidationErrors() throws Exception {
        mockMvc.perform(post("/roles/"+ROLE_ID)
                .param("name", "")
                .param("description", ROLE_DESCRIPTION))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("role"))
                .andExpect(model().hasErrors()).andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("role", "name"))
                .andExpect(view().name(VIEW_EDIT_ROLE));

        verify(securityService, times(0)).updateRole(Matchers.isA(Role.class));

    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void updateRole_RoleNotFound_ShouldAddExceptionToModelAndRender500View() throws Exception {
        when(securityService.updateRole(Matchers.isA(Role.class))).thenThrow(new JCartException());

        mockMvc.perform(post("/roles/"+ROLE_ID)
                .param("name", ROLE_NAME)
                .param("description", ROLE_DESCRIPTION))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(model().attributeExists("exception"))
                .andExpect(view().name(VIEW_500));
    }
}