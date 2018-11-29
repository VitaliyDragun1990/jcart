package com.revenat.jcart.admin.security;


import com.revenat.jcart.entities.Permission;
import com.revenat.jcart.entities.Role;
import com.revenat.jcart.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Wrapper for SpringSecurity User
 */
public class AuthenticatedUser extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 1L;
    private User user;

    public AuthenticatedUser(User user) {
        super(user.getEmail(), user.getPassword(), getAuthorities(user));
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(User user) {
        Set<String> roleAndPermissions = new HashSet<>();
        List<Role> roles = user.getRoles();

        for (Role role : roles) {
            roleAndPermissions.add(role.getName());
            List<Permission> permissions = role.getPermissions();
            for (Permission permission : permissions) {
                roleAndPermissions.add("ROLE_" + permission.getName());
            }
        }
        String[] roleNames = new String[roleAndPermissions.size()];

        return AuthorityUtils.createAuthorityList(roleAndPermissions.toArray(roleNames));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AuthenticatedUser that = (AuthenticatedUser) o;

        return user != null ? user.equals(that.user) : that.user == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}
