package com.revenat.jcart.core.security;

import com.revenat.jcart.core.entities.Permission;
import com.revenat.jcart.core.entities.Role;

public class PermissionBuilder {
    private Permission permission;

    private PermissionBuilder() {
        this.permission = new Permission();
    }

    public static PermissionBuilder getBuilder() {
        return new PermissionBuilder();
    }

    public Permission build() {
        return this.permission;
    }

    public PermissionBuilder withId(Integer id) {
        this.permission.setId(id);
        return this;
    }

    public PermissionBuilder withName(String name) {
        this.permission.setName(name);
        return this;
    }

    public PermissionBuilder withDescription(String description) {
        this.permission.setDescription(description);
        return this;
    }

    public PermissionBuilder withRole(Role role) {
        this.permission.getRoles().add(role);
        return this;
    }
}
