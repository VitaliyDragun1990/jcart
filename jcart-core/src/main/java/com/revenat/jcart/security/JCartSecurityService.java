package com.revenat.jcart.security;

import com.revenat.jcart.exceptions.JCartException;
import com.revenat.jcart.entities.Permission;
import com.revenat.jcart.entities.Role;
import com.revenat.jcart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Facade to all Security Related Methods
 */
@Service
@Transactional
public class JCartSecurityService implements SecurityService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RoleRepository roleRepository;

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String resetPassword(String email) {
        User user = findUserByEmail(email);
        if (user == null) {
            throw new JCartException("Invalid email address");
        }
        String uuid = UUID.randomUUID().toString();
        user.setPasswordResetToken(uuid);
        return uuid;
    }

    public void updatePassword(String email, String token, String password) {
        User user = findUserByEmail(email);
        if (user == null) {
            throw new JCartException("Invalid email address");
        }
        if (!StringUtils.hasText(token) || !token.equals(user.getPasswordResetToken())) {
            throw new JCartException("Invalid password reset token");
        }
        user.setPassword(password);
        user.setPasswordResetToken(null);
    }

    public boolean verifyPasswordResetToken(String email, String token) {
        User user = findUserByEmail(email);
        if (user == null) {
            throw new JCartException("Invalid email address");
        }
        return StringUtils.hasText(token) && token.equals(user.getPasswordResetToken());
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    public Role createRole(Role role) {
        Role roleByName = getRoleByName(role.getName());
        if (roleByName != null) {
            throw new JCartException("Role " + role.getName() + " already exists");
        }
        List<Permission> persistedPermissions = evaluateRolePermissions(role);

        role.setPermissions(persistedPermissions);
        return roleRepository.save(role);
    }

    public Role updateRole(Role role) {
        Role persistedRole = getRoleById(role.getId());
        if (persistedRole == null) {
            throw new JCartException("Role " + role.getId() + " doesn't exist");
        }
        persistedRole.setDescription(role.getDescription());

        List<Permission> updatedPermissions = evaluateRolePermissions(role);

        persistedRole.setPermissions(updatedPermissions);
        return roleRepository.save(persistedRole);
    }

    private List<Permission> evaluateRolePermissions(Role role) {
        List<Permission> persistedPermissions = new ArrayList<>();
        List<Permission> permissions = role.getPermissions();

        if (permissions != null && !permissions.isEmpty()) {
            permissions.stream()
                    .filter(p -> p.getId() != null)
                    .forEach(p -> persistedPermissions.add(permissionRepository.findOne(p.getId())));
        }
        return persistedPermissions;
    }

    public Role getRoleById(Integer id) {
        return roleRepository.findOne(id);
    }

    public User getUserById(Integer id) {
        return userRepository.findOne(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        User userByEmail = findUserByEmail(user.getEmail());
        if (userByEmail != null) {
            throw new JCartException("Email " + user.getEmail() + " already in use");
        }
        List<Role> persistedRoles = evaluateUserRoles(user);

        user.setRoles(persistedRoles);

        return userRepository.save(user);
    }

    public User updateUser(User user) {
        User persistedUser = getUserById(user.getId());
        if (persistedUser == null) {
            throw new JCartException("User " + user.getId() + " doesn't exist");
        }

        List<Role> updatedRoles = evaluateUserRoles(user);

        persistedUser.setRoles(updatedRoles);

        return userRepository.save(persistedUser);
    }

    private List<Role> evaluateUserRoles(User user) {
        List<Role> persistedRoles = new ArrayList<>();
        List<Role> roles = user.getRoles();
        if (roles != null && !roles.isEmpty()) {

            roles.stream().filter(r -> r.getId() != null)
                    .forEach(r -> persistedRoles.add(roleRepository.getOne(r.getId())));
        }
        return persistedRoles;
    }
}
