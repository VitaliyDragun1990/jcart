package com.revenat.jcart.admin.web.validators;

import com.revenat.jcart.admin.web.commands.UserCommand;
import com.revenat.jcart.entities.User;
import com.revenat.jcart.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.revenat.jcart.admin.web.utils.MessageCodes.ERROR_EXISTS;

@Component
public class UserValidator implements Validator {

    @Autowired
    protected SecurityService securityService;

    @Override
    public boolean supports(Class<?> aClass) {
        return UserCommand.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserCommand user = (UserCommand) target;
        String email = user.getEmail();
        User userByEmail = securityService.findUserByEmail(email);
        if (userByEmail != null) {
            errors.rejectValue("email", ERROR_EXISTS, new Object[]{email}, "Email " + email + " already in use.");
        }
    }
}
