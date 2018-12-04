package com.revenat.jcart.core.security;

import com.revenat.jcart.core.entities.Permission;
import com.revenat.jcart.core.entities.Role;
import com.revenat.jcart.core.entities.User;

import java.util.List;

public interface SecurityService {
    User findUserByEmail(String email);

    String resetPassword(String email);

    void updatePassword(String email, String token, String password);

    boolean verifyPasswordResetToken(String email, String token);

    List<Permission> getAllPermissions();

    List<Role> getAllRoles();

    Role getRoleByName(String roleName);

    Role createRole(Role role);

    Role updateRole(Role role);

    Role getRoleById(Integer id);

    User getUserById(Integer id);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

}
