package com.revenat.jcart.security;

import com.revenat.jcart.exceptions.JCartException;
import com.revenat.jcart.entities.Role;
import com.revenat.jcart.entities.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SecurityServiceTest {

    private static final String DUMMY_EMAIL = "dummy@gmail.com";
    private static final String VALID_TOKEN = "valid_token";
    private static final String WRONG_TOKEN = "wrong_token";
    private static final String ROLE_NAME = "some_name";
    private static final String ROLE_DESCRIPTION = "some description";

    @Mock
    private UserRepository userRepository;
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private RoleRepository roleRepository;

    private User dummyUser = new User();
    @Spy
    private Role dummyRole = new Role();

    @InjectMocks
    private JCartSecurityService securityService;

    @Test
    public void findUserByEmail() {
        securityService.findUserByEmail(DUMMY_EMAIL);

        verify(userRepository, times(1)).findByEmail(DUMMY_EMAIL);
    }

    @Test
    public void resetPasswordPositive() {
        when(userRepository.findByEmail(DUMMY_EMAIL)).thenReturn(dummyUser);

        String token = securityService.resetPassword(DUMMY_EMAIL);

        assertNotNull(token);
        assertThat(token, equalTo(dummyUser.getPasswordResetToken()));
    }

    @Test(expected = JCartException.class)
    public void resetPasswordNegativeWhenWrongEmail() {
        securityService.resetPassword(DUMMY_EMAIL);
    }

    @Test(expected = JCartException.class)
    public void updatePasswordNegativeWhenWrongEmail() {
        securityService.resetPassword(DUMMY_EMAIL);
    }

    @Test(expected = JCartException.class)
    public void updatePasswordNegativeWhenTokenMismatch() {
        dummyUser.setPasswordResetToken(VALID_TOKEN);
        when(userRepository.findByEmail(DUMMY_EMAIL)).thenReturn(dummyUser);

        securityService.updatePassword(DUMMY_EMAIL, WRONG_TOKEN, "new_password");
    }

    @Test
    public void updatePasswordPositive() {
        String new_password = "new_password";
        dummyUser.setPasswordResetToken(VALID_TOKEN);
        when(userRepository.findByEmail(DUMMY_EMAIL)).thenReturn(dummyUser);

        securityService.updatePassword(DUMMY_EMAIL, VALID_TOKEN, new_password);

        assertThat(dummyUser.getPassword(), equalTo(new_password));
        assertNull(dummyUser.getPasswordResetToken());
    }

    @Test
    public void verifyPasswordTokenPositive() {
        when(userRepository.findByEmail(DUMMY_EMAIL)).thenReturn(dummyUser);
        dummyUser.setPasswordResetToken(VALID_TOKEN);

        boolean isValid = securityService.verifyPasswordResetToken(DUMMY_EMAIL, VALID_TOKEN);

        assertTrue(isValid);
    }

    @Test(expected = JCartException.class)
    public void verifyPasswordTokenThrowsExceptionWhenWrongEmail() {
        securityService.verifyPasswordResetToken(DUMMY_EMAIL, VALID_TOKEN);
    }

    @Test
    public void verifyPasswordTokenNegativeWhenTokenMismatch() {
        when(userRepository.findByEmail(DUMMY_EMAIL)).thenReturn(dummyUser);
        dummyUser.setPasswordResetToken(VALID_TOKEN);

        boolean isValid = securityService.verifyPasswordResetToken(DUMMY_EMAIL, WRONG_TOKEN);

        assertFalse(isValid);
    }

    @Test
    public void getAllPermissionsPositive() {
        securityService.getAllPermissions();

        verify(permissionRepository, times(1)).findAll();
    }

    @Test
    public void getAllRolesPositive() {
        securityService.getAllRoles();

        verify(roleRepository, times(1)).findAll();
    }

    @Test
    public void getRoleByNamePositive() {
        securityService.getRoleByName(ROLE_NAME);

        verify(roleRepository, times(1)).findByName(ROLE_NAME);
    }

    @Test
    public void createRolePositive() {
        dummyRole.setName(ROLE_NAME);
        securityService.createRole(dummyRole);

        verify(roleRepository, times(1)).findByName(ROLE_NAME);
        verify(dummyRole, times(1)).setPermissions(anyList());
        verify(roleRepository, times(1)).save(dummyRole);
    }

    @Test(expected = JCartException.class)
    public void createRoleThrowsExceptionNameOccupied() {
        dummyRole.setName(ROLE_NAME);
        when(roleRepository.findByName(ROLE_NAME)).thenReturn(new Role());

        securityService.createRole(dummyRole);
    }

    @Test
    public void updateRolePositive() {
        Role updatedRole = new Role();
        dummyRole.setId(1);
        dummyRole.setDescription(ROLE_DESCRIPTION);
        when(roleRepository.findOne(1)).thenReturn(updatedRole);

        securityService.updateRole(dummyRole);

        verify(roleRepository, times(1)).findOne(1);
        assertThat(updatedRole.getDescription(), equalTo(ROLE_DESCRIPTION));
        verify(roleRepository, times(1)).save(updatedRole);
    }

    @Test
    public void getRoleByIdPositive() {
        securityService.getRoleById(1);

        verify(roleRepository, times(1)).findOne(1);
    }

    @Test
    public void getUserByIdPositive() {
        securityService.getUserById(1);

        verify(userRepository, times(1)).findOne(1);
    }

    @Test
    public void getAllUsersPositive() {
        securityService.getAllUsers();

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void createUserPositive() {
        dummyUser.setRoles(null);
        dummyUser.setEmail(DUMMY_EMAIL);

        securityService.createUser(dummyUser);

        verify(userRepository, times(1)).findByEmail(DUMMY_EMAIL);
        verify(userRepository, times(1)).save(dummyUser);
        assertNotNull(dummyUser.getRoles());
    }

    @Test(expected = JCartException.class)
    public void createUserThrowsExceptionEmailOccupied() {
        dummyUser.setEmail(DUMMY_EMAIL);
        when(userRepository.findByEmail(DUMMY_EMAIL)).thenReturn(new User());

        securityService.createUser(dummyUser);
    }

    @Test
    public void updateUserPositive() {
        dummyUser.setId(1);
        dummyUser.setRoles(null);
        when(userRepository.findOne(1)).thenReturn(dummyUser);

        securityService.updateUser(dummyUser);

        verify(userRepository, times(1)).findOne(1);
        verify(userRepository, times(1)).save(dummyUser);
        verifyNoMoreInteractions(userRepository);
        assertNotNull(dummyUser.getRoles());
    }
}