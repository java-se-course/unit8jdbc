package com.epam.course.java.se;

import lombok.Data;

import javax.sql.DataSource;

public interface WithDbProperties {
    @Data
    class DbProps {
//        private final String url;
//        private final String userName;
//        private final String password;
        private final DataSource dataSource;
    }

    DbProps getProperties();
}
