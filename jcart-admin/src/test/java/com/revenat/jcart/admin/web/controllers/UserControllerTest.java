package com.revenat.jcart.admin.web.controllers;

import com.revenat.config.MockSecurityServiceConfig;
import com.revenat.config.SecurityConfigurer;
import com.revenat.config.TestConfig;
import com.revenat.jcart.JCartAdminApplication;
import com.revenat.jcart.admin.web.controllers.builders.RoleBuilder;
import com.revenat.jcart.admin.web.controllers.builders.UserBuilder;
import com.revenat.jcart.core.entities.Role;
import com.revenat.jcart.core.entities.User;
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
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
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
public class UserControllerTest {

    private static final String AUTH_USER_EMAIL = "john@gmail.com";

    private static final Integer USER_ID = 1;
    private static final Integer ROLE_ID = 1;
    private static final String USER_NAME = "dummy";
    private static final String USER_EMAIL = "dummy@gmail.com";
    private static final String USER_PASSWORD = "test";
    private static final String HEADER_TITLE = "Manage Users";
    private static final String VIEW_USERS = "users/users";
    private static final String VIEW_CREATE_USER = "users/create_user";
    private static final String VIEW_EDIT_USER = "users/edit_user";
    private static final String VIEW_404 = "error/404";
    private static final String VIEW_500 = "error/500";


    @Autowired
    private WebApplicationContext context;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserController controller;

    private MockMvc mockMvc;

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

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void getHeaderTitle_ReturnsHeaderTitle() {
        assertThat(controller.getHeaderTitle(), equalTo(HEADER_TITLE));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void roleList_ShouldReturnListWithAllRoleEntries() {
        Role role = RoleBuilder.getBuilder().build();
        when(securityService.getAllRoles()).thenReturn(Arrays.asList(role));

        List<Role> allRoles = controller.rolesList();

        verify(securityService, times(1)).getAllRoles();
        assertThat(allRoles, hasSize(1));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void listUsers_ShouldAddUserEntriesToModelAndRenderUsersView() throws Exception {
        User user = UserBuilder.getBuilder().withId(USER_ID).withName(USER_NAME).withEmail(USER_EMAIL).build();
        when(securityService.getAllUsers()).thenReturn(Arrays.asList(user));

        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", hasSize(1)))
                .andExpect(model().attribute("users", hasItem(
                        allOf(
                                hasProperty("id", is(USER_ID)),
                                hasProperty("name", is(USER_NAME)),
                                hasProperty("email", is(USER_EMAIL))
                        )
                )))
                .andExpect(view().name(VIEW_USERS));

        verify(securityService, times(1)).getAllUsers();
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void createUserForm_ShouldAddUserEntryToModelAndRenderCreateUserView() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name(VIEW_CREATE_USER));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void createUser_NewUserEntry_ShouldCreateUserAndRenderUsersView() throws Exception {
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(securityService.findUserByEmail(USER_EMAIL)).thenReturn(null);
        when(securityService.createUser(Matchers.isA(User.class))).thenAnswer(
                (Answer<User>) invocationOnMock -> (User) invocationOnMock.getArguments()[0]);

        mockMvc.perform(post("/users")
                .param("name", USER_NAME)
                .param("email", USER_EMAIL)
                .param("password", USER_PASSWORD))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/users"));

        verify(securityService, times(1)).findUserByEmail(USER_EMAIL);
        verify(securityService, times(1)).createUser(userCaptor.capture());
        User createdUser = userCaptor.getValue();
        assertThat(createdUser.getName(), equalTo(USER_NAME));
        assertThat(createdUser.getEmail(), equalTo(USER_EMAIL));
        assertNotNull("User password should not be null", createdUser.getPassword());
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void createUser_EmailAlreadyTaken_ShouldRenderCreateUserViewAndReturnValidationErrors() throws Exception {
        when(securityService.findUserByEmail(USER_EMAIL)).thenReturn(UserBuilder.getBuilder().build());

        mockMvc.perform(post("/users")
                .param("name", USER_NAME)
                .param("email", USER_EMAIL)
                .param("password", USER_PASSWORD))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("user", "email"))
                .andExpect(view().name(VIEW_CREATE_USER));

        verify(securityService, times(1)).findUserByEmail(USER_EMAIL);
        verify(securityService, times(0)).createUser(Matchers.isA(User.class));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void editUserForm_UserFound_ShouldAddUserEntryToModelAndRenderEditUserView() throws Exception {
        User user = UserBuilder.getBuilder()
                .withId(USER_ID)
                .withName(USER_NAME)
                .withEmail(USER_EMAIL)
                .withPassword(USER_PASSWORD)
                .build();
        when(securityService.getUserById(USER_ID)).thenReturn(user);

        mockMvc.perform(get("/users/"+USER_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user",
                        allOf(
                                hasProperty("name", is(USER_NAME)),
                                hasProperty("email", is(USER_EMAIL)),
                                hasProperty("id", is(USER_ID)),
                                hasProperty("password", is(USER_PASSWORD))
                )))
                .andExpect(view().name(VIEW_EDIT_USER));

        verify(securityService, times(1)).getUserById(USER_ID);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void editUserForm_UserNotFound_ShouldAddExceptionToModelAndRender404View() throws Exception {
        when(securityService.getUserById(USER_ID)).thenReturn(null);

        mockMvc.perform(get("/users/"+USER_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists("exception"))
                .andExpect(view().name(VIEW_404));

        verify(securityService, times(1)).getUserById(USER_ID);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void updateUser_UpdatedUserEntry_ShouldUpdateUserAndRenderUsersView() throws Exception {
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(securityService.updateUser(Matchers.any(User.class))).thenAnswer(
                (Answer<User>) invocationOnMock -> (User) invocationOnMock.getArguments()[0]);

        mockMvc.perform(post("/users/"+USER_ID)
                .param("name", USER_NAME)
                .param("email", USER_EMAIL)
                .param("password", USER_PASSWORD)
                .param("roles[0].id", ROLE_ID.toString())
                .param("_roles[0].id", "on"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/users"));

        verify(securityService, times(1)).updateUser(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        List<Role> userRoles = updatedUser.getRoles().stream().filter(r -> r.getId() != null).collect(Collectors.toList());
        assertThat(updatedUser.getName(), equalTo(USER_NAME));
        assertThat(updatedUser.getEmail(), equalTo(USER_EMAIL));
        assertThat(updatedUser.getPassword(), equalTo(USER_PASSWORD));
        assertThat(userRoles, hasSize(1));
        assertThat(userRoles.get(0).getId(), equalTo(ROLE_ID));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void updateUser_EmptyName_ShouldRenderEditUserViewAndReturnValidationErrors() throws Exception {
        mockMvc.perform(post("/users/"+USER_ID)
                .param("name", "")
                .param("email", USER_EMAIL)
                .param("password", USER_PASSWORD))
                .andDo(print())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("user", "name"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_EDIT_USER));

        verify(securityService, times(0)).updateUser(Matchers.isA(User.class));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void updateUser_UserNotFound_ShouldAddExceptionToModelAndRender500View() throws Exception {
        when(securityService.updateUser(Matchers.isA(User.class))).thenThrow(new JCartException());

        mockMvc.perform(post("/users/"+USER_ID)
                .param("name", USER_NAME)
                .param("email", USER_EMAIL)
                .param("password", USER_PASSWORD))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(view().name(VIEW_500));
    }
}