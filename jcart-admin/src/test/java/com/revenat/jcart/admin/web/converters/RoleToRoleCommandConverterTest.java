package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.RoleCommand;
import com.revenat.jcart.core.entities.Permission;
import com.revenat.jcart.core.entities.Role;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class RoleToRoleCommandConverterTest {

    private RoleToRoleCommandConverter converter = new RoleToRoleCommandConverter();

    @Test
    public void convertRoleToRoleCommand() {
        Permission permission = new Permission();
        permission.setId(1);
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_TEST");
        role.setDescription("Test");
        role.setPermissions(new ArrayList<Permission>() {{add(permission);}});

        RoleCommand command = converter.convert(role);

        assertThat(command.getId(), equalTo(role.getId()));
        assertThat(command.getName(), equalTo(role.getName()));
        assertThat(command.getDescription(), equalTo(role.getDescription()));
        assertThat(command.getPermissions(), equalTo(role.getPermissions()));
    }
}