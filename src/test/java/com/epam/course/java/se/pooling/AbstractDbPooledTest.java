package com.epam.course.java.se.pooling;

import com.epam.course.java.se.ConnectionPool;
import lombok.Builder;
import lombok.Data;
import org.junit.Test;

import java.sql.*;

public abstract class AbstractDbPooledTest {

    protected abstract ConnectionPool getConnectionPool();

    @Test
    public void test() throws SQLException, ClassNotFoundException, InterruptedException {
        try (Connection connection = getConnectionPool().getConnection()) {
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
                System.out.printf("AuthorDto[id: %d, name: %s]\n", resultSet.getInt("id"), resultSet.getString("name"));
            }
        }
    }

}
