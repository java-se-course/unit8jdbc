package com.epam.course.java.se.datasource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.BeforeClass;

import javax.sql.DataSource;

public class H2DataSourceTest extends AbstractDbDataSourceTest {
    private static DataSource ds;
    @BeforeClass
    public static void startup() {
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setUrl("jdbc:h2:mem:test_mem");
        ds.setUser("uname");
        ds.setPassword("pwd");
        H2DataSourceTest.ds = ds;
    }

    @Override
    protected DataSource getDataSource() {
        return ds;
    }
}
