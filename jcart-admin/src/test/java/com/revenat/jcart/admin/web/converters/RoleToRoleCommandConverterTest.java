package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.RoleCommand;
import com.revenat.jcart.core.entities.Permission;
import com.revenat.jcart.core.entities.Role;
import org.junit.Test;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class RoleToRoleCommandConverterTest {
    private static final int ID = 1;
    private static final String NAME = "ROLE_TEST";
    private static final String DESCRIPTION = "Test";

    private RoleToRoleCommandConverter converter = new RoleToRoleCommandConverter();

    @Test
    public void convertRoleToRoleCommand() {
        Permission permission = new Permission();
        Role role = createRoleWithPermission(ID, NAME, DESCRIPTION, permission);

        RoleCommand command = converter.convert(role);

        assertThat(command.getId(), equalTo(ID));
        assertThat(command.getName(), equalTo(NAME));
        assertThat(command.getDescription(), equalTo(DESCRIPTION));
        assertThat(command.getPermissions(), hasSize(1));
    }

    private Role createRoleWithPermission(Integer id, String name, String description, Permission permission) {
        Role role = new Role();
        role.setId(id);
        role.setName(name);
        role.setDescription(description);
        role.getPermissions().add(permission);

        return role;
    }
}