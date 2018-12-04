package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.UserCommand;
import com.revenat.jcart.core.entities.User;
import org.springframework.core.convert.converter.Converter;

public class UserToUserCommandConverter implements Converter<User, UserCommand> {

    @Override
    public UserCommand convert(User user) {
        UserCommand command = new UserCommand();

        command.setId(user.getId());
        command.setName(user.getName());
        command.setEmail(user.getEmail());
        command.setPassword(user.getPassword());
        command.setRoles(user.getRoles());

        return command;
    }
}
