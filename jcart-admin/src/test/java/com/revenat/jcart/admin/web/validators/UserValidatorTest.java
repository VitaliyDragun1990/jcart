package com.revenat.jcart.admin.web.validators;

import com.revenat.jcart.admin.web.commands.UserCommand;
import com.revenat.jcart.core.entities.Role;
import com.revenat.jcart.core.entities.User;
import com.revenat.jcart.core.security.SecurityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserValidatorTest {

    private static final String EMAIL = "test@gmai.com";
    @Mock
    private SecurityService securityService;
    @Mock
    private Errors errors;

    @InjectMocks
    private UserValidator validator;

    @Test
    public void testValidate_OK() {
        UserCommand user = new UserCommand();
        user.setEmail(EMAIL);

        validator.validate(user, errors);

        verifyZeroInteractions(errors);
    }

    @Test
    public void testValidate_Error() {
        UserCommand user = new UserCommand();
        user.setEmail(EMAIL);
        when(securityService.findUserByEmail(EMAIL)).thenReturn(new User());

        validator.validate(user, errors);

        verify(errors, times(1)).rejectValue(anyString(), anyString(), any(), anyString());
    }

    @Test
    public void testSupport_Ok() {
        boolean result = validator.supports(UserCommand.class);

        assertTrue(result);
    }

    @Test
    public void testSupport_Fail() {
        boolean result = validator.supports(Role.class);

        assertFalse(result);
    }
}