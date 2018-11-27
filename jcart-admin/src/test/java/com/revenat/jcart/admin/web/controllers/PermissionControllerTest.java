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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {JCartAdminApplication.class})
@WebAppConfiguration
public class PermissionControllerTest {

    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithUserDetails("john@gmail.com")
    public void testListPermissions_Success() throws Exception {
        mockMvc.perform(get("/permissions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("permissions"))
                .andExpect(view().name("permissions/permissions"));
    }

    @Test
    @WithUserDetails("jack@gmail.com")
    public void testListPermissions_Redirect_Not_Allowed() throws Exception {
        mockMvc.perform(get("/permissions"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(forwardedUrl("/403"));
    }
}