package com.epam.course.java.se.withdata;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.BeforeClass;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class H2TestImpl extends AbstractDbTest {
    private static DataSource ds;
    @BeforeClass
    public static void startup() {
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setUrl("jdbc:h2:mem:test_mem;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        ds.setUser("uname");
        ds.setPassword("pwd");
        H2TestImpl.ds = ds;
    }

    @Override
    protected DataSource getDataSource() {
        return ds;
    }

    @Override
    protected void dropSchema() throws SQLException {
        try (Connection connection = getDataSource().getConnection()) {
            final Statement statement = connection.createStatement();
            statement.execute("DROP SCHEMA unit8schema;");
        }
    }
}
