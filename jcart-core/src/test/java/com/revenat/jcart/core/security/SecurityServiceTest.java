package com.revenat.jcart.core.security;

import com.revenat.jcart.core.entities.Permission;
import com.revenat.jcart.core.exceptions.JCartException;
import com.revenat.jcart.core.entities.Role;
import com.revenat.jcart.core.entities.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SecurityServiceTest {

    private static final String USER_EMAIL = "dummy@gmail.com";
    private static final String VALID_TOKEN = "valid_token";
    private static final String WRONG_TOKEN = "wrong_token";
    private static final String ROLE_NAME = "some_name";
    private static final String ROLE_DESCRIPTION = "some description";
    private static final String PERMISSION_DESCRIPTION = "some description here";
    private static final int ROLE_ID = 1;
    private static final int PERMISSION_ID = 2;
    private static final String PASSWORD = "test";
    private static final int USER_ID = 1;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private JCartSecurityService securityService;

    @Test
    public void findUserByEmail_ValidEmailGiven_ShouldReturnUser() {
        User user = UserBuilder.getBuilder().withEmail(USER_EMAIL).build();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(user);

        User returnedUser = securityService.findUserByEmail(USER_EMAIL);

        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
        assertThat(returnedUser.getEmail(), equalTo(USER_EMAIL));
    }

    @Test
    public void findUserByEmail_InvalidEmailGiven_ShouldReturnNull() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(null);

        User returnedUser = securityService.findUserByEmail(USER_EMAIL);

        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
        assertThat("User should be null if searched with wrong email.", returnedUser, equalTo(null));
    }

    @Test
    public void resetPassword_ValidEmail_PasswordResetTokenSet() {
        User user = UserBuilder.getBuilder().build();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(user);

        String token = securityService.resetPassword(USER_EMAIL);

        assertNotNull(token);
        assertThat(token, equalTo(user.getPasswordResetToken()));
        verify(userRepository, timeout(1)).findByEmail(USER_EMAIL);
    }

    @Test(expected = JCartException.class)
    public void resetPassword_InvalidEmail_ExceptionThrown() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        securityService.resetPassword(USER_EMAIL);
    }

    @Test(expected = JCartException.class)
    public void updatePassword_InvalidEmail_ExceptionThrown() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        securityService.updatePassword(USER_EMAIL, VALID_TOKEN, PASSWORD);
    }

    @Test(expected = JCartException.class)
    public void updatePassword_InvalidToken_ExceptionThrown() {
        User user = UserBuilder.getBuilder().withPasswordResetToken(VALID_TOKEN).build();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(user);

        securityService.updatePassword(USER_EMAIL, WRONG_TOKEN, PASSWORD);
    }

    @Test
    public void updatePassword_ValidData_NewPasswordSet() {
        User user = UserBuilder.getBuilder().withPasswordResetToken(VALID_TOKEN).build();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(user);

        securityService.updatePassword(USER_EMAIL, VALID_TOKEN, PASSWORD);

        assertThat(user.getPassword(), equalTo(PASSWORD));
        assertNull("Reset token should be null after password has been updated.",
                user.getPasswordResetToken());
    }

    @Test
    public void verifyPasswordToken_ValidToken_PositiveResult() {
        User user = UserBuilder.getBuilder().withPasswordResetToken(VALID_TOKEN).build();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(user);

        boolean isValid = securityService.verifyPasswordResetToken(USER_EMAIL, VALID_TOKEN);

        assertTrue("Result should be true for valid token.", isValid);
    }

    @Test(expected = JCartException.class)
    public void verifyPasswordToken_WrongEmail_ExceptionThrown() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(null);

        securityService.verifyPasswordResetToken(USER_EMAIL, VALID_TOKEN);
    }

    @Test
    public void verifyPasswordToken_InvalidTokenGiven_NegativeResult() {
        User user = UserBuilder.getBuilder().withPasswordResetToken(VALID_TOKEN).build();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(user);

        boolean isValid = securityService.verifyPasswordResetToken(USER_EMAIL, WRONG_TOKEN);

        assertFalse("Result should be false for invalid token.", isValid);
    }

    @Test
    public void getAllPermissions_ShouldReturnAllPermissions() {
        when(permissionRepository.findAll()).thenReturn(new ArrayList<>());

        List<Permission> permissions = securityService.getAllPermissions();

        assertThat(permissions, hasSize(0));
        verify(permissionRepository, times(1)).findAll();
    }

    @Test
    public void getAllRoles_ShouldReturnAllRoles() {
        when(roleRepository.findAll()).thenReturn(new ArrayList<>());

        List<Role> roles = securityService.getAllRoles();

        assertThat(roles, hasSize(0));
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    public void getRoleByName_ValidName_ShouldReturnRole() {
        Role role = RoleBuilder.getBuilder().withName(ROLE_NAME).build();
        when(roleRepository.findByName(ROLE_NAME)).thenReturn(role);

        Role roleByName = securityService.getRoleByName(ROLE_NAME);

        assertThat(roleByName.getName(), equalTo(ROLE_NAME));
        verify(roleRepository, times(1)).findByName(ROLE_NAME);
    }

    @Test
    public void getRoleByName_WrongName_ShouldReturnNull() {
        when(roleRepository.findByName(ROLE_NAME)).thenReturn(null);

        Role roleByName = securityService.getRoleByName(ROLE_NAME);

        assertThat(roleByName, equalTo(null));
        verify(roleRepository, times(1)).findByName(ROLE_NAME);
    }

    @Test
    public void createRole_UniqueName_NewRoleCreated() {
        Permission permission = PermissionBuilder.getBuilder()
                .withId(PERMISSION_ID)
                .withDescription(PERMISSION_DESCRIPTION)
                .build();
        Role role = RoleBuilder.getBuilder()
                .withName(ROLE_NAME)
                .withPermission(permission)
                .build();
        when(roleRepository.findByName(anyString())).thenReturn(null);
        when(permissionRepository.findOne(PERMISSION_ID)).thenReturn(permission);
        when(roleRepository.save(isA(Role.class))).thenAnswer(
                (Answer<Role>) invocationOnMock -> (Role) invocationOnMock.getArguments()[0]);

        Role newRole = securityService.createRole(role);

        assertThat(newRole.getName(), equalTo(ROLE_NAME));
        assertThat(newRole.getPermissions(), hasSize(1));
        assertThat(newRole.getPermissions().get(0), hasProperty("description", equalTo(PERMISSION_DESCRIPTION)));
        verify(roleRepository, times(1)).findByName(ROLE_NAME);
        verify(roleRepository, times(1)).save(isA(Role.class));
    }

    @Test(expected = JCartException.class)
    public void createRole_OccupiedNameGiven_ExceptionThrown() {
        Role role = RoleBuilder.getBuilder().withName(ROLE_NAME).build();
        when(roleRepository.findByName(ROLE_NAME)).thenReturn(role);

        securityService.createRole(role);
    }

    @Test
    public void updateRole_RoleWithValidId_RoleUpdated() {
        Permission permission = PermissionBuilder.getBuilder().withId(PERMISSION_ID).build();
        Role persistedRole = RoleBuilder.getBuilder().build();
        Role updateHolder = RoleBuilder.getBuilder()
                .withId(ROLE_ID)
                .withDescription(ROLE_DESCRIPTION)
                .withPermission(permission)
                .build();
        when(roleRepository.findOne(ROLE_ID)).thenReturn(persistedRole);
        when(permissionRepository.findOne(PERMISSION_ID)).thenReturn(permission);
        when(roleRepository.save(isA(Role.class))).thenAnswer(
                (Answer<Role>) invocationOnMock -> (Role) invocationOnMock.getArguments()[0]);

        securityService.updateRole(updateHolder);

        assertThat(persistedRole, hasProperty("description", equalTo(ROLE_DESCRIPTION)));
        assertThat(persistedRole.getPermissions(), hasSize(1));
        verify(roleRepository, times(1)).findOne(1);
        verify(roleRepository, times(1)).save(isA(Role.class));
    }

    @Test(expected = JCartException.class)
    public void updateRole_RoleWithInvalidId_ExceptionThrown() {
        Role role = RoleBuilder.getBuilder().withId(ROLE_ID).build();
        when(roleRepository.findOne(ROLE_ID)).thenReturn(null);

        securityService.updateRole(role);
    }

    @Test
    public void getRoleById_CorrectId_ShouldReturnRole() {
        Role role = RoleBuilder.getBuilder().withId(ROLE_ID).build();
        when(roleRepository.findOne(ROLE_ID)).thenReturn(role);

        Role roleById = securityService.getRoleById(ROLE_ID);

        assertThat(roleById.getId(), equalTo(ROLE_ID));
        verify(roleRepository, times(1)).findOne(ROLE_ID);
    }

    @Test
    public void getRoleById_WrongId_ShouldReturnNull() {
        when(roleRepository.findOne(ROLE_ID)).thenReturn(null);

        Role roleById = securityService.getRoleById(ROLE_ID);

        assertThat(roleById, equalTo(null));
        verify(roleRepository, times(1)).findOne(ROLE_ID);
    }

    @Test
    public void getUserById_CorrectId_ShouldReturnUser() {
        User user = UserBuilder.getBuilder().withId(USER_ID).build();
        when(userRepository.findOne(USER_ID)).thenReturn(user);

        User userById = securityService.getUserById(USER_ID);

        assertThat(userById.getId(), equalTo(USER_ID));
        verify(userRepository, times(1)).findOne(USER_ID);
    }

    @Test
    public void getUserById_WrongId_ShouldReturnNull() {
        when(userRepository.findOne(USER_ID)).thenReturn(null);

        User userById = securityService.getUserById(USER_ID);

        assertThat(userById, equalTo(null));
        verify(userRepository, times(1)).findOne(USER_ID);
    }

    @Test
    public void getAllUsers_ShouldReturnAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(UserBuilder.getBuilder().build());
        users.add(UserBuilder.getBuilder().build());
        when(userRepository.findAll()).thenReturn(users);

        List<User> allUsers = securityService.getAllUsers();

        assertThat(allUsers, hasSize(2));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void createUser_UniqueEmailGiven_UserCreated() {
        Role role = RoleBuilder.getBuilder().withId(ROLE_ID).build();
        User user = UserBuilder.getBuilder().withEmail(USER_EMAIL).withRole(role).build();
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userRepository.save(isA(User.class))).thenAnswer(
                (Answer<User>) invocationOnMock -> (User) invocationOnMock.getArguments()[0]);

        User newUser = securityService.createUser(user);

        assertThat(newUser.getRoles(), hasSize(1));
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
        verify(userRepository, times(1)).save(isA(User.class));
    }

    @Test(expected = JCartException.class)
    public void createUser_EmailAlreadyTaken_ExceptionThrown() {
        User user = UserBuilder.getBuilder().withEmail(USER_EMAIL).build();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(user);

        securityService.createUser(user);
    }

    @Test
    public void updateUser_UserWithValidId_UserUpdated() {
        Role role = RoleBuilder.getBuilder().withId(ROLE_ID).build();
        User persistedUser = UserBuilder.getBuilder().withRole(role).build();
        User updateHolder = UserBuilder.getBuilder().withId(USER_ID).build();
        when(userRepository.findOne(USER_ID)).thenReturn(persistedUser);
        when(userRepository.save(isA(User.class))).thenAnswer(
                (Answer<User>) invocationOnMock -> (User) invocationOnMock.getArguments()[0]);

        User updatedUser = securityService.updateUser(updateHolder);

        assertThat(updatedUser.getRoles(), hasSize(0));
        verify(userRepository, times(1)).findOne(USER_ID);
        verify(userRepository, times(1)).save(isA(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test(expected = JCartException.class)
    public void updateUser_UserWithWrongId_ExceptionThrown() {
        User user = UserBuilder.getBuilder().withId(USER_ID).build();
        when(userRepository.findOne(USER_ID)).thenReturn(null);

        securityService.updateUser(user);
    }
}