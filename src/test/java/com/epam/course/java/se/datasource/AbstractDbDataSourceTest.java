package com.epam.course.java.se.datasource;

import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;

public abstract class AbstractDbDataSourceTest {


    protected abstract DataSource getDataSource();

    @Test
    public void test() throws SQLException, ClassNotFoundException {

         try (Connection connection = getDataSource().getConnection()) {
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
