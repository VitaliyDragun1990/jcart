package com.revenat.jcart.core;

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

    @Test
    public void getCatalog_AssertThatDbSchemaExists() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String schema = connection.getCatalog();
            assertEquals("JCART", schema);
        }
    }
}