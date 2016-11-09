package com.epam.course.java.se.pooling;

import com.epam.course.java.se.ConnectionPool;
import com.epam.course.java.se.ConnectionPoolImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class H2PooledTestImpl extends AbstractDbPooledTest {

    private static ConnectionPool connectionPool;

    @BeforeClass
    public static void startup() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        final List<Connection> connections = new ArrayList<>(4);

        for (int i = 0; i < 4; i++) {
            connections.add(DriverManager.getConnection("jdbc:h2:mem:test_mem", "un", "pwd"));
        }

        connectionPool = new ConnectionPoolImpl(connections);
    }

    @Override
    protected ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    @AfterClass
    public static void teardown() throws Exception {
        connectionPool.close();
    }
}
