package com.revenat.jcart.admin.web.validators;

import com.revenat.jcart.entities.Role;
import com.revenat.jcart.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RoleValidator implements Validator {

    @Autowired
    protected SecurityService securityService;

    @Override
    public boolean supports(Class<?> aClass) {
        return Role.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Role role = (Role) target;
        String roleName = role.getName();
        Role roleByName = securityService.getRoleByName(roleName);
        if (roleByName != null) {
            errors.rejectValue("name", "errors.exists",
                    new Object[]{roleName}, "Role " + roleName + " already exists");
        }
    }
}
