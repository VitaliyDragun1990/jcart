package com.revenat.jcart.admin.web.validators;

import com.revenat.jcart.admin.web.commands.RoleCommand;
import com.revenat.jcart.core.entities.Permission;
import com.revenat.jcart.core.entities.Role;
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
public class RoleValidatorTest {

    private static final String ROLE_NAME = "ROLE_DUMMY";

    @Mock
    private SecurityService securityService;
    @Mock
    private Errors errors;

    @InjectMocks
    private RoleValidator validator;

    @Test
    public void validate_UniqueName_NoErrors() {
        RoleCommand role = new RoleCommand();
        role.setName(ROLE_NAME);
        when(securityService.getRoleByName(ROLE_NAME)).thenReturn(null);

        validator.validate(role, errors);

        verifyZeroInteractions(errors);
    }

    @Test
    public void validate_TakenName_ValidationError() {
        RoleCommand role = new RoleCommand();
        role.setName(ROLE_NAME);
        when(securityService.getRoleByName(ROLE_NAME)).thenReturn(new Role());

        validator.validate(role, errors);

        verify(errors, times(1)).rejectValue(anyString(), anyString(), any(), anyString());
    }

    @Test
    public void test_ValidClass_ReturnsTrue() {
        boolean result = validator.supports(RoleCommand.class);

        assertTrue(result);
    }

    @Test
    public void support_WrongClass_ReturnsFalse() {
        boolean result = validator.supports(Permission.class);

        assertFalse(result);
    }
}