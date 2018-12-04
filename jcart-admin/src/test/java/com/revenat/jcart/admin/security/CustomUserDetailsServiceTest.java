package com.revenat.jcart.admin.security;

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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private CustomUserDetailsService customService;

    @Test
    public void loadsUserIfUsernameExists() {
        String validUsername = "john@gmail.com";
        User user = new User();
        user.setEmail(validUsername);
        user.setPassword("password");
        when(securityService.findUserByEmail(validUsername)).thenReturn(user);

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) customService.loadUserByUsername(validUsername);

        assertThat(authenticatedUser.getUser(), equalTo(user));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void throwsExceptionIfInvalidUsername() {
        String invalidUsername = "dummy@gmail.com";

        customService.loadUserByUsername(invalidUsername);
    }
}