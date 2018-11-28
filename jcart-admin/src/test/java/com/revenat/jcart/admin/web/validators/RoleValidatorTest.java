package com.revenat.jcart.admin.web.validators;

import com.revenat.jcart.admin.web.commands.RoleCommand;
import com.revenat.jcart.entities.Role;
import com.revenat.jcart.security.SecurityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RoleValidatorTest {

    private static final String ROLE_NAME = "ROLE_DUMMY";

    @Mock
    private SecurityService securityService;
    @Mock
    private Errors errors;

    @InjectMocks
    private RoleValidator validator;

    @Test
    public void testValidate_OK() {
        RoleCommand role = new RoleCommand();
        role.setName(ROLE_NAME);

        validator.validate(role, errors);

        verifyZeroInteractions(errors);
    }

    @Test
    public void testValidate_Error() {
        RoleCommand role = new RoleCommand();
        role.setName(ROLE_NAME);
        when(securityService.getRoleByName(ROLE_NAME)).thenReturn(new Role());

        validator.validate(role, errors);

        verify(errors, times(1)).rejectValue(anyString(), anyString(), any(), anyString());
    }
}