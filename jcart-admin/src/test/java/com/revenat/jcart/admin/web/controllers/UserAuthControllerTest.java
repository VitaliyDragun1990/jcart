package com.revenat.jcart.admin.web.controllers;

import com.revenat.config.MockCatalogServiceConfig;
import com.revenat.config.MockCustomerServiceConfig;
import com.revenat.config.MockImageServiceConfig;
import com.revenat.jcart.JCartAdminApplication;
import com.revenat.config.MockSecurityAndEmailConfig;
import com.revenat.jcart.admin.web.controllers.builders.UserBuilder;
import com.revenat.jcart.core.common.services.EmailService;
import com.revenat.jcart.core.entities.User;
import com.revenat.jcart.core.exceptions.JCartException;
import com.revenat.jcart.core.security.SecurityService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(
        classes = {JCartAdminApplication.class, MockCatalogServiceConfig.class,
                MockImageServiceConfig.class, MockSecurityAndEmailConfig.class,
                MockCustomerServiceConfig.class}
        )
@WebAppConfiguration
@ActiveProfiles("test")
public class UserAuthControllerTest {

    private static final String USER_EMAIL = "dummy@gmail.com";
    private static final String RESET_TOKEN = "password_reset_token";
    private static final String USER_PASSWORD = "dummy_password";
    private static final String VIEW_FORGOT_PASSWORD = "public/forgotPwd";
    private static final String VIEW_RESET_PASSWORD = "public/resetPwd";
    private static final String ERROR_MESSAGE = "Error occurred.";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserAuthController controller;

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
        Mockito.reset(securityService);
    }

    @Test
    public void getHeaderTitle_ShouldReturnHeaderTitle() {
        assertThat(controller.getHeaderTitle(), equalTo("User"));
    }

    @Test
    public void forgotPassword_ShouldRenderForgotPasswordView() throws Exception {
        mockMvc.perform(get("/forgotPwd"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_FORGOT_PASSWORD));
    }

    @Test
    public void handleForgotPassword_UserFound_ShouldSetResetTokenAndSendEmail() throws Exception {
        User user = UserBuilder.getBuilder().withEmail(USER_EMAIL).build();
        when(securityService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        mockMvc.perform(post("/forgotPwd").param("email", USER_EMAIL))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgotPwd"))
                .andExpect(flash().attributeCount(1))
                .andExpect(flash().attributeExists("msg"));

        verify(securityService, times(1)).findUserByEmail(USER_EMAIL);
        verify(securityService, times(1)).resetPassword(USER_EMAIL);
        verify(emailService, times(1)).sendEmail(contains(USER_EMAIL), anyString(), anyString());
    }

    @Test
    public void handleForgotPassword_UserNotFound_ShouldNotSendEmailAndRenderForgotPasswordView() throws Exception {
        when(securityService.findUserByEmail(anyString())).thenReturn(null);
        when(securityService.resetPassword(anyString())).thenThrow(new JCartException(ERROR_MESSAGE));

        mockMvc.perform(post("/forgotPwd").param("email", USER_EMAIL))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgotPwd"))
                .andExpect(flash().attributeCount(1))
                .andExpect(flash().attributeExists("msg"));

        verifyZeroInteractions(emailService);
    }

    @Test
    public void resetPassword_ValidEmailAndToken_AddEmailAndTokenToModelAndRenderResetPasswordView() throws Exception {
        when(securityService.verifyPasswordResetToken(USER_EMAIL, RESET_TOKEN)).thenReturn(true);

        mockMvc.perform(get("/resetPwd").param("email", USER_EMAIL).param("token", RESET_TOKEN))
                .andDo(print())
                .andExpect(model().attribute("email", is(USER_EMAIL)))
                .andExpect(model().attribute("token", is(RESET_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_RESET_PASSWORD));

        verify(securityService, atLeastOnce()).verifyPasswordResetToken(USER_EMAIL, RESET_TOKEN);
    }

    @Test
    public void resetPassword_InvalidEmail_ShouldAddFlashMessageAndRedirectToLoginPage() throws Exception {
        when(securityService.verifyPasswordResetToken(USER_EMAIL, RESET_TOKEN))
                .thenThrow(new JCartException(ERROR_MESSAGE));

        mockMvc.perform(get("/resetPwd").param("email", USER_EMAIL).param("token", RESET_TOKEN))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeCount(1))
                .andExpect(flash().attributeExists("msg"))
                .andExpect(flash().attribute("msg", containsString(ERROR_MESSAGE)));

        verify(securityService, times(1)).verifyPasswordResetToken(USER_EMAIL, RESET_TOKEN);
    }

    @Test
    public void resetPassword_InvalidToken_ShouldAddFlashMessageAndRedirectToLoginPage() throws Exception {
        when(securityService.verifyPasswordResetToken(USER_EMAIL, RESET_TOKEN)).thenReturn(false);

        mockMvc.perform(get("/resetPwd").param("email", USER_EMAIL).param("token", RESET_TOKEN))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeCount(1))
                .andExpect(flash().attributeExists("msg"));

        verify(securityService, times(1)).verifyPasswordResetToken(USER_EMAIL, RESET_TOKEN);
    }

    @Test
    public void handleResetPassword_ValidCredentials_UpdatePasswordAndRedirectToLoginPage() throws Exception {
        mockMvc.perform(post("/resetPwd")
                    .param("email", USER_EMAIL)
                    .param("token", RESET_TOKEN)
                    .param("password", USER_PASSWORD)
                    .param("confPassword", USER_PASSWORD))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("msg"));

        verify(securityService)
                .updatePassword(contains(USER_EMAIL), contains(RESET_TOKEN), anyString());
    }

    @Test
    public void handleResetPassword_PasswordsMismatch_AddModelAttributesAndRenderResetPasswordView() throws Exception {

        mockMvc.perform(post("/resetPwd")
                .param("email", USER_EMAIL)
                .param("token", RESET_TOKEN)
                .param("password", USER_PASSWORD)
                .param("confPassword", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("email", "token", "msg"))
                .andExpect(model().attribute("email", is(USER_EMAIL)))
                .andExpect(model().attribute("token", is(RESET_TOKEN)))
                .andExpect(view().name(VIEW_RESET_PASSWORD));

        verifyZeroInteractions(securityService);
    }

    @Test
    public void handleResetPassword_WrongEmail_NotUpdatePasswordAndRedirectToLoginPage() throws Exception {
        doThrow(new JCartException(ERROR_MESSAGE)).when(securityService).updatePassword(anyString(), anyString(), anyString());

        mockMvc.perform(post("/resetPwd")
                .param("email", USER_EMAIL)
                .param("token", RESET_TOKEN)
                .param("password", USER_PASSWORD)
                .param("confPassword", USER_PASSWORD))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("msg"))
                .andExpect(flash().attribute("msg", containsString(ERROR_MESSAGE)));

        verify(securityService)
                .updatePassword(anyString(), anyString(), anyString());
    }
}