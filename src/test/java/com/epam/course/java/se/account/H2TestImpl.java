package com.epam.course.java.se.account;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.BeforeClass;

import javax.sql.DataSource;

public class H2TestImpl extends AbstractAccountsTest {
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

}
