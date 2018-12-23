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
    private static final String VALID_EMAIL = "john@gmail.com";
    private static final String UNKNOWN_EMAIL = "unknown@gmail.com";

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByEmail_ValidEmailGiven_UserReturned() {
        User user = userRepository.findByEmail(VALID_EMAIL);

        assertNotNull(user);
        assertThat(user.getEmail(), equalTo(VALID_EMAIL));
    }

    @Test
    public void findByEmail_UnknownEmailGiven_NullReturned() {
        User user = userRepository.findByEmail(UNKNOWN_EMAIL);

        assertNull(user);
    }
}