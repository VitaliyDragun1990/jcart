package com.revenat.jcart.admin.web.validators;

import com.revenat.jcart.admin.web.commands.RoleCommand;
import com.revenat.jcart.core.entities.Role;
import com.revenat.jcart.core.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.revenat.jcart.admin.web.utils.MessageCodes.ERROR_EXISTS;

@Component
public class RoleValidator implements Validator {

    @Autowired
    protected SecurityService securityService;

    @Override
    public boolean supports(Class<?> aClass) {
        return RoleCommand.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RoleCommand command = (RoleCommand) target;
        String roleName = command.getName();
        Role roleByName = securityService.getRoleByName(roleName);
        if (roleByName != null) {
            errors.rejectValue("name", ERROR_EXISTS,
                    new Object[]{roleName}, "Role " + roleName + " already exists.");
        }
    }
}
