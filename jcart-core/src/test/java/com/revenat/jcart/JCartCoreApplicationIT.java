package com.revenat.jcart;

import com.revenat.jcart.common.services.SimpleEmailService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JCartCoreApplication.class)
public class JCartCoreApplicationIT {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private SimpleEmailService emailService;

    @Test
    public void testDummy() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String schema = connection.getCatalog();
            assertEquals("JCART", schema);
        }
    }

    @Test
    @Ignore
    public void testSendEmail() {
        emailService.sendEmail("visperboy@gmail.com", "JCart - Test Mail", "This is a test email from JCart");
    }
}