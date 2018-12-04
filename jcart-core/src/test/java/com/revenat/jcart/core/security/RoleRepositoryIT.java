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

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void findByNamePositiveWhenExists() {
        String roleName = "ROLE_ADMIN";

        Role role = roleRepository.findByName(roleName);

        assertNotNull(role);
    }

    @Test
    public void findByNameNegativeWhenNotExists() {
        String wrongName = "ROLE_IMPOSTOR";

        Role role = roleRepository.findByName(wrongName);

        assertNull(role);
    }
}