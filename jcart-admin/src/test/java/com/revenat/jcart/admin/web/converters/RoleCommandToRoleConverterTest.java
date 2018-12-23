package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.RoleCommand;
import com.revenat.jcart.core.entities.Permission;
import com.revenat.jcart.core.entities.Role;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class RoleCommandToRoleConverterTest {
    private static final int ID = 1;
    private static final String NAME = "ROLE_TEST";
    private static final String DESCRIPTION = "Test";

    private RoleCommandToRoleConverter converter = new RoleCommandToRoleConverter();

    @Test
    public void convertRoleCommandToRole() {
        Permission permission = new Permission();
        RoleCommand command = createRoleCommandWithPermission(ID, NAME, DESCRIPTION, permission);

        Role role = converter.convert(command);

        assertThat(role.getId(), equalTo(ID));
        assertThat(role.getName(), equalTo(NAME));
        assertThat(role.getDescription(), equalTo(DESCRIPTION));
        assertThat(role.getPermissions(), hasSize(1));
    }

    private RoleCommand createRoleCommandWithPermission(Integer id, String name, String description, Permission permission) {
        RoleCommand command = new RoleCommand();
        command.setId(id);
        command.setName(name);
        command.setDescription(description);
        command.getPermissions().add(permission);
        return command;
    }
}