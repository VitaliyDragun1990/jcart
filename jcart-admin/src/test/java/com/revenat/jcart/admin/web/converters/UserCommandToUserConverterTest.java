package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.UserCommand;
import com.revenat.jcart.core.entities.Role;
import com.revenat.jcart.core.entities.User;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class UserCommandToUserConverterTest {
    private static final int ID = 1;
    private static final String NAME = "Jack";
    private static final String EMAIL = "jack@gmail.com";
    private static final String PASSWORD = "test";

    private UserCommandToUserConverter converter = new UserCommandToUserConverter();

    @Test
    public void convertUserCommandToUser() {
        Role role = new Role();
        UserCommand command = createUserCommandWithRole(ID, NAME, EMAIL, PASSWORD, role);

        User user = converter.convert(command);

        assertThat(user.getId(), equalTo(ID));
        assertThat(user.getName(), equalTo(NAME));
        assertThat(user.getEmail(), equalTo(EMAIL));
        assertThat(user.getPassword(), equalTo(PASSWORD));
        assertThat(user.getRoles(), hasSize(1));
    }

    private UserCommand createUserCommandWithRole(Integer id, String name, String email, String password, Role role) {
        UserCommand command = new UserCommand();
        command.setId(id);
        command.setName(name);
        command.setEmail(email);
        command.setPassword(password);
        command.getRoles().add(role);

        return command;
    }
}