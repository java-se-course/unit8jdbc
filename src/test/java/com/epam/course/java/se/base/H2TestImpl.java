package com.epam.course.java.se.base;

public class H2TestImpl extends AbstractDbTest {

    @Override
    protected DbConfiguration getDbConfiguration() throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
        return DbConfiguration.builder()
                .url("jdbc:h2:mem:test_mem")
                .username("un")
                .password("pwd")
                .build();
    }
}
