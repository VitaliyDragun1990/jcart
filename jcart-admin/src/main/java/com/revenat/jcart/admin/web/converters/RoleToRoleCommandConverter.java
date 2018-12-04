package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.RoleCommand;
import com.revenat.jcart.core.entities.Role;
import org.springframework.core.convert.converter.Converter;

public class RoleToRoleCommandConverter implements Converter<Role, RoleCommand> {

    @Override
    public RoleCommand convert(Role role) {
        RoleCommand command = new RoleCommand();

        command.setId(role.getId());
        command.setName(role.getName());
        command.setDescription(role.getDescription());
        command.setPermissions(role.getPermissions());

        return command;
    }
}
