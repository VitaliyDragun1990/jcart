package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.UserCommand;
import com.revenat.jcart.core.entities.Role;
import com.revenat.jcart.core.entities.User;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class UserToUserCommandConverterTest {
    private static final int ID = 1;
    private static final String NAME = "Jack";
    private static final String EMAIL = "jack@gmail.com";
    private static final String PASSWORD = "test";

    private UserToUserCommandConverter converter = new UserToUserCommandConverter();

    @Test
    public void convertUserToUserCommand() {
        Role role = new Role();
        User user = createUserWithRole(ID, NAME, EMAIL, PASSWORD, role);

        UserCommand command = converter.convert(user);

        assertThat(command.getId(), equalTo(ID));
        assertThat(command.getName(), equalTo(NAME));
        assertThat(command.getEmail(), equalTo(EMAIL));
        assertThat(command.getPassword(), equalTo(PASSWORD));
        assertThat(command.getRoles(), hasSize(1));
    }

    private User createUserWithRole(Integer id, String name, String email, String password, Role role) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.getRoles().add(role);

        return user;
    }
}