package com.epam.course.java.se.base;

import lombok.Builder;
import lombok.Data;
import org.junit.Test;

import java.io.IOException;
import java.sql.*;

public abstract class AbstractDbTest {

    @Data
    @Builder
    protected static class DbConfiguration{
        private final String url;
        private final String username;
        private final String password;
    }

    protected abstract DbConfiguration getDbConfiguration() throws ClassNotFoundException;

    @Test
    public void test() throws SQLException, ClassNotFoundException {
        final DbConfiguration config = getDbConfiguration();

        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword())) {
            final Statement statement = connection.createStatement();

            statement.execute("CREATE SCHEMA unit8schema;");

            statement.execute(
                    "CREATE TABLE unit8schema.author ( id INTEGER PRIMARY KEY, name VARCHAR(255) NOT NULL);"
            );

            final int cnt = statement.executeUpdate(
                    "INSERT INTO unit8schema.author(id, name)" +
                            " SELECT 1, 'Терри Пратчетт'" +
                            "UNION ALL SELECT 2, 'Иммануил Кант';"
            );

            System.out.println("Inserted: " + cnt);

            final ResultSet resultSet = statement.executeQuery("SELECT id, name FROM unit8schema.author");

            while (resultSet.next()) {
                System.out.printf("Author[id: %d, name: %s]\n", resultSet.getInt("id"), resultSet.getString("name"));
            }
        }
    }

}
