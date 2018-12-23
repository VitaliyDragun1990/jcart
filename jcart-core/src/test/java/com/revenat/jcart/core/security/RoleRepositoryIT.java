package com.revenat.jcart.core.security;

import com.revenat.jcart.core.JCartCoreApplication;
import com.revenat.jcart.core.entities.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@SpringApplicationConfiguration(classes = JCartCoreApplication.class)
public class RoleRepositoryIT {
    private static final String CORRECT_ROLE_NAME = "ROLE_ADMIN";
    private static final String WRONG_ROLE_NAME = "ROLE_IMPOSTOR";

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void findByName_ValidName_RoleReturned() {
        Role role = roleRepository.findByName(CORRECT_ROLE_NAME);

        assertNotNull("Should not be null when searched with correct name.", role);
    }

    @Test
    public void findByName_InvalidName_NullReturned() {
        Role role = roleRepository.findByName(WRONG_ROLE_NAME);

        assertNull("Should be null when searched with wrong name.", role);
    }
}