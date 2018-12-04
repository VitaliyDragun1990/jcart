package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.UserCommand;
import com.revenat.jcart.core.entities.User;
import org.springframework.core.convert.converter.Converter;

public class UserCommandToUserConverter implements Converter<UserCommand, User> {

    @Override
    public User convert(UserCommand command) {
        User user = new User();

        user.setId(command.getId());
        user.setName(command.getName());
        user.setEmail(command.getEmail());
        user.setPassword(command.getPassword());
        user.setRoles(command.getRoles());

        return user;
    }
}
