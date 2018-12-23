package com.revenat.builders;

import com.revenat.jcart.core.entities.Permission;
import com.revenat.jcart.core.entities.Role;
import com.revenat.jcart.core.entities.User;

public class RoleBuilder {
    private Role role;

    private RoleBuilder() {
        this.role = new Role();
    }

    public static RoleBuilder getBuilder() {
        return new RoleBuilder();
    }

    public Role build() {
        return this.role;
    }

    public RoleBuilder withId(Integer id) {
        this.role.setId(id);
        return this;
    }

    public RoleBuilder withName(String name) {
        this.role.setName(name);
        return this;
    }

    public RoleBuilder withDescription(String description) {
        this.role.setDescription(description);
        return this;
    }

    public RoleBuilder withUser(User user) {
        this.role.getUsers().add(user);
        return this;
    }

    public RoleBuilder withPermission(Permission permission) {
        this.role.getPermissions().add(permission);
        return this;
    }
}
