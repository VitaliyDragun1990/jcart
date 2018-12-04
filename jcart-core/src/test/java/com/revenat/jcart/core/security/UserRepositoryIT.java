package com.revenat.jcart.core.security;

import com.revenat.jcart.core.JCartCoreApplication;
import com.revenat.jcart.core.entities.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@SpringApplicationConfiguration(classes = JCartCoreApplication.class)
public class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByEmailPositiveWhenValidEmail() {
        String email = "john@gmail.com";

        User user = userRepository.findByEmail(email);

        assertNotNull(user);
        assertThat(user.getEmail(), equalTo(email));
    }

    @Test
    public void findByEmailNegativeWhenUnknownEmail() {
        String unknownEmail = "unknown@gmail.com";

        User user = userRepository.findByEmail(unknownEmail);

        assertNull(user);
    }
}