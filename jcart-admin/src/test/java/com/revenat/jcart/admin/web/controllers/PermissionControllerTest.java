package com.revenat.jcart.admin.web.controllers;

import com.revenat.config.MockSecurityServiceConfig;
import com.revenat.config.SecurityConfigurer;
import com.revenat.config.TestConfig;
import com.revenat.jcart.JCartAdminApplication;
import com.revenat.jcart.admin.web.controllers.builders.PermissionBuilder;
import com.revenat.jcart.core.security.SecurityService;
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

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
        classes = {JCartAdminApplication.class, MockSecurityServiceConfig.class,
                TestConfig.class}
)
@WebAppConfiguration
@ActiveProfiles("test")
public class PermissionControllerTest {
    private static final String USER_WITH_AUTHORITY_EMAIL = "john@gmail.com";
    private static final String USER_WITHOUT_AUTHORITY_EMAIL = "anna@gmail.com";

    private static final String PERMISSION_NAME = "Permission name";
    private static final String PERMISSION_DESCRIPTION = "Permission Description";
    private static final String VIEW_PERMISSIONS = "permissions/permissions";
    private static final String HEADER_TITLE = "Manage Permissions";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PermissionController controller;

    @Autowired
    private SecurityService securityService;


    private MockMvc mockMvc;

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpMockAuthenticatedUser() {
        Mockito.reset(securityService);
        SecurityConfigurer.configureUserWithFullAuthorities(securityService, USER_WITH_AUTHORITY_EMAIL);
        SecurityConfigurer.configureUserWithoutAuthorities(securityService, USER_WITHOUT_AUTHORITY_EMAIL);
    }

    @Test
    @WithUserDetails(USER_WITH_AUTHORITY_EMAIL)
    public void getHeaderTitle_ReturnsHeaderTitle() {
        assertThat(controller.getHeaderTitle(), equalTo(HEADER_TITLE));
    }

    @Test
    @WithUserDetails(USER_WITH_AUTHORITY_EMAIL)
    public void listPermissions_ShouldAddPermissionEntriesToModelAndRenderPermissionsView() throws Exception {
        when(securityService.getAllPermissions()).thenReturn(Arrays.asList(
                PermissionBuilder.getBuilder().withName(PERMISSION_NAME).withDescription(PERMISSION_DESCRIPTION)
                .build()
        ));

        mockMvc.perform(get("/permissions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("permissions"))
                .andExpect(model().attribute("permissions", hasSize(1)))
                .andExpect(model().attribute("permissions", hasItem(
                        allOf(
                                hasProperty("name", is(PERMISSION_NAME)),
                                hasProperty("description", is(PERMISSION_DESCRIPTION))
                        )
                )))
                .andExpect(view().name(VIEW_PERMISSIONS));
    }

    @Test
    @WithUserDetails(USER_WITHOUT_AUTHORITY_EMAIL)
    public void listPermissions_UserWithoutRequiredAuthority_ShouldRenderAccessDeniedView() throws Exception {
        mockMvc.perform(get("/permissions"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(forwardedUrl("/403"));
    }
}