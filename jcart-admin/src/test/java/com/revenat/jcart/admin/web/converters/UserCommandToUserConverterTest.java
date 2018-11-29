package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.UserCommand;
import com.revenat.jcart.entities.Role;
import com.revenat.jcart.entities.User;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class UserCommandToUserConverterTest {

    private UserCommandToUserConverter converter = new UserCommandToUserConverter();

    @Test
    public void convertUserCommandToUser() {
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_DUMMY");
        UserCommand command = new UserCommand();
        command.setId(1);
        command.setName("Jack");
        command.setEmail("jack@gmail.com");
        command.setPassword("test");
        command.setRoles(new ArrayList<Role>(){{add(role);}});

        User user = converter.convert(command);

        assertThat(user.getId(), equalTo(command.getId()));
        assertThat(user.getName(), equalTo(command.getName()));
        assertThat(user.getEmail(), equalTo(command.getEmail()));
        assertThat(user.getPassword(), equalTo(command.getPassword()));
        assertThat(user.getRoles(), equalTo(command.getRoles()));
    }
}