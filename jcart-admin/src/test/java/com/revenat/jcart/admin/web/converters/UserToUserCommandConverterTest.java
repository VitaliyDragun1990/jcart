package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.UserCommand;
import com.revenat.jcart.core.entities.Role;
import com.revenat.jcart.core.entities.User;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class UserToUserCommandConverterTest {

    private UserToUserCommandConverter converter = new UserToUserCommandConverter();

    @Test
    public void convertUserToUserCommand() {
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_DUMMY");
        User user = new User();
        user.setId(1);
        user.setName("Jack");
        user.setEmail("jack@gmail.com");
        user.setPassword("test");
        user.setRoles(new ArrayList<Role>(){{add(role);}});

        UserCommand command = converter.convert(user);

        assertThat(command.getId(), equalTo(user.getId()));
        assertThat(command.getName(), equalTo(user.getName()));
        assertThat(command.getEmail(), equalTo(user.getEmail()));
        assertThat(command.getPassword(), equalTo(user.getPassword()));
        assertThat(command.getRoles(), equalTo(user.getRoles()));
    }
}