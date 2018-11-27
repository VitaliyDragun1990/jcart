package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.JCartAdminApplication;
import com.revenat.jcart.admin.config.MockTemplateConfig;
import com.revenat.jcart.common.services.EmailService;
import com.revenat.jcart.entities.User;
import com.revenat.jcart.security.SecurityService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(classes = {JCartAdminApplication.class, MockTemplateConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class UserAuthControllerTest {

    private static final String DUMMY_EMAIL = "dummy@gmail.com";
    private static final String DUMMY_TOKEN = "password_reset_token";
    private static final String DUMMY_PASSWORD = "dummy_password";

    private User dummyUser;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private EmailService emailService;

    // 1st part of the replacement for SpringJUnit4ClassRule
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    // 2nd part of the replacement for SpringJUnitClassRule
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Before
    public void setMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void prepareMocks() {
        dummyUser = new User();
        dummyUser.setEmail(DUMMY_EMAIL);
        dummyUser.setName("Dummy");
    }

    @Test
    public void testForgotPasswordGet() throws Exception {
        mockMvc.perform(get("/forgotPwd"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/forgotPwd"));
    }

    @Test
    public void testForgotPasswordPost() throws Exception {
        when(securityService.findUserByEmail(DUMMY_EMAIL)).thenReturn(dummyUser);

        mockMvc.perform(post("/forgotPwd").param("email", DUMMY_EMAIL))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgotPwd"))
                .andExpect(flash().attributeCount(1))
                .andExpect(flash().attributeExists("msg"));

        verify(securityService, times(1)).findUserByEmail(DUMMY_EMAIL);
        verify(securityService, times(1)).resetPassword(DUMMY_EMAIL);
        verify(emailService, times(1)).sendEmail(contains(DUMMY_EMAIL), anyString(), anyString());
    }

    @Test
    public void testResetPasswordGet_OK() throws Exception {
        when(securityService.verifyPasswordResetToken(DUMMY_EMAIL, DUMMY_TOKEN)).thenReturn(true);

        mockMvc.perform(get("/resetPwd").param("email", DUMMY_EMAIL).param("token",DUMMY_TOKEN))
                .andDo(print())
                .andExpect(model().attribute("email", DUMMY_EMAIL))
                .andExpect(model().attribute("token", DUMMY_TOKEN))
                .andExpect(status().isOk())
                .andExpect(view().name("public/resetPwd"));

        verify(securityService, atLeastOnce()).verifyPasswordResetToken(DUMMY_EMAIL, DUMMY_TOKEN);
    }

    @Test
    public void testResetPasswordGet_InvalidToken() throws Exception {
        when(securityService.verifyPasswordResetToken(DUMMY_EMAIL, DUMMY_TOKEN)).thenReturn(false);

        mockMvc.perform(get("/resetPwd").param("email", DUMMY_EMAIL).param("token", DUMMY_TOKEN))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeCount(1))
                .andExpect(flash().attributeExists("msg"));

        verify(securityService, atLeastOnce()).verifyPasswordResetToken(anyString(), anyString());
    }

    @Test
    public void testResetPasswordPost_OK() throws Exception {
        mockMvc.perform(post("/resetPwd")
                    .param("email", DUMMY_EMAIL)
                    .param("token", DUMMY_TOKEN)
                    .param("password", DUMMY_PASSWORD)
                    .param("confPassword", DUMMY_PASSWORD))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("msg"));

        verify(securityService)
                .updatePassword(contains(DUMMY_EMAIL), contains(DUMMY_TOKEN), anyString());
    }

    @Test
    public void testResetPasswordPost_Negative() throws Exception {
        String confirmPassword = "wrong_confirm_password";

        mockMvc.perform(post("/resetPwd")
                .param("email", DUMMY_EMAIL)
                .param("token", DUMMY_TOKEN)
                .param("password", DUMMY_PASSWORD)
                .param("confPassword", confirmPassword))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("public/resetPwd"))
                .andExpect(model().attributeExists("email", "token", "msg"));

        verifyZeroInteractions(securityService);

    }
}