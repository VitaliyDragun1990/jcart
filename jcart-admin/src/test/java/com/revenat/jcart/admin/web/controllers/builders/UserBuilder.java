package com.revenat.jcart.admin.web.controllers.builders;

import com.revenat.jcart.core.entities.Role;
import com.revenat.jcart.core.entities.User;

import java.util.List;

public class UserBuilder {
    private User user;

    private UserBuilder() {
        this.user = new User();
    }

    public static UserBuilder getBuilder() {
        return new UserBuilder();
    }

    public User build() {
        return this.user;
    }

    public UserBuilder withId(Integer id) {
        this.user.setId(id);
        return this;
    }

    public UserBuilder withName(String name) {
        this.user.setName(name);
        return this;
    }

    public UserBuilder withEmail(String email) {
        this.user.setEmail(email);
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.user.setPassword(password);
        return this;
    }

    public UserBuilder withPasswordResetToken(String passwordResetToken) {
        this.user.setPasswordResetToken(passwordResetToken);
        return this;
    }

    public UserBuilder withRole(Role role) {
        List<Role> roles = this.user.getRoles();
        roles.add(role);
        return this;
    }
}
