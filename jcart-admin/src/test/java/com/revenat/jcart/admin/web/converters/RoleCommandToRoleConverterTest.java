package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.RoleCommand;
import com.revenat.jcart.entities.Permission;
import com.revenat.jcart.entities.Role;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class RoleCommandToRoleConverterTest {

    private RoleCommandToRoleConverter converter = new RoleCommandToRoleConverter();

    @Test
    public void convertRoleCommandToRole() {
        Permission permission = new Permission();
        permission.setId(1);
        RoleCommand command = new RoleCommand();
        command.setId(1);
        command.setName("ROLE_TEST");
        command.setDescription("Test");
        command.setPermissions(new ArrayList<Permission>() {{add(permission);}});

        Role role = converter.convert(command);

        assertThat(role.getId(), equalTo(command.getId()));
        assertThat(role.getName(), equalTo(command.getName()));
        assertThat(role.getDescription(), equalTo(command.getDescription()));
        assertThat(role.getPermissions(), equalTo(command.getPermissions()));
    }
}