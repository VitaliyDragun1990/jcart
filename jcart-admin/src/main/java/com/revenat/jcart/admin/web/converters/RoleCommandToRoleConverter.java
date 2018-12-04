package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.RoleCommand;
import com.revenat.jcart.core.entities.Role;
import org.springframework.core.convert.converter.Converter;

public class RoleCommandToRoleConverter implements Converter<RoleCommand, Role> {

    @Override
    public Role convert(RoleCommand command) {
        Role role = new Role();

        role.setId(command.getId());
        role.setName(command.getName());
        role.setDescription(command.getDescription());
        role.setPermissions(command.getPermissions());

        return role;
    }
}
