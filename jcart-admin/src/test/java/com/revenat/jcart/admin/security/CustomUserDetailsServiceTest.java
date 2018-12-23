package com.revenat.jcart.admin.security;

import com.revenat.jcart.admin.web.controllers.builders.UserBuilder;
import com.revenat.jcart.core.entities.User;
import com.revenat.jcart.core.security.SecurityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserDetailsServiceTest {

    private static final String USER_EMAIL = "john@gmail.com";
    private static final String USER_PASSWORD = "password";

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private CustomUserDetailsService customService;

    @Test
    public void loadUserByUsername_UserFound_UserDetailsReturned() {
        User user = UserBuilder.getBuilder().withEmail(USER_EMAIL).withPassword(USER_PASSWORD).build();
        when(securityService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) customService.loadUserByUsername(USER_EMAIL);

        verify(securityService, times(1)).findUserByEmail(USER_EMAIL);
        assertThat(authenticatedUser.getUser(), equalTo(user));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername_UserNotFound_ExceptionThrown() {
        when(securityService.findUserByEmail(USER_EMAIL)).thenReturn(null);

        customService.loadUserByUsername(USER_EMAIL);
    }
}