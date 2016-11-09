package com.epam.course.java.se;

import java.sql.Connection;

public interface ConnectionPool extends AutoCloseable {
    Connection getConnection() throws InterruptedException;
}
