package com.epam.course.java.se;

import org.junit.Test;

import java.sql.*;

public class PostgreSqlBase {

    @Test
    public void test1() throws SQLException, ClassNotFoundException {
        final String url = "jdbc:postgresql://localhost:5432/postgres";
//        final String url = "jdbc:h2:mem:test_mem";
        final String username = "postgres";
        final String password = "postgres";

        Class.forName("org.postgresql.Driver");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            final PreparedStatement clear = connection.prepareStatement("DROP SCHEMA IF EXISTS unit8schema CASCADE ;");
            clear.execute();

            final PreparedStatement createSchema = connection.prepareStatement("CREATE SCHEMA unit8schema;");

            createSchema.execute();

            final PreparedStatement createAuthor = connection.prepareStatement(
                    "CREATE TABLE unit8schema.author ( id INTEGER PRIMARY KEY, name VARCHAR(255) NOT NULL);"
            );

            createAuthor.execute();

            final PreparedStatement insert =
                    connection.prepareStatement(
                            "INSERT INTO unit8schema.author(id, name) VALUES (1, 'Терри Пратчетт');" +
                                    "INSERT INTO unit8schema.author(id, name) VALUES (2, 'Иммануил Кант');"
                    );

            final int cnt = insert.executeUpdate();

            System.out.println("Inserted: " + cnt);

            final PreparedStatement select = connection.prepareStatement(
                    "SELECT id, name FROM unit8schema.author ORDER BY id",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            final ResultSet resultSet = select.executeQuery();

            while (resultSet.next()) {
                System.out.printf("Author[id: %d, name: %s]\n", resultSet.getInt("id"), resultSet.getString("name"));
            }

            resultSet.beforeFirst();

            while (resultSet.next()) {
                System.out.printf("Author[id: %d, name: %s]\n", resultSet.getInt("id"), resultSet.getString("name"));
            }

            resultSet.afterLast();
            while (resultSet.previous()) {
                System.out.printf("Author[id: %d, name: %s]\n", resultSet.getInt("id"), resultSet.getString("name"));
                resultSet.updateString("name", resultSet.getString("name") + "!");
                resultSet.updateRow();
            }

            resultSet.moveToInsertRow();
            resultSet.updateInt("id", 3);
            resultSet.updateString("name", "Additional");
            resultSet.insertRow();

            final ResultSet resultSet2 = select.executeQuery();

            System.out.println();
            System.out.println("Result Set 2");

            while (resultSet2.next()) {
                System.out.printf("Author[id: %d, name: %s]\n", resultSet2.getInt("id"), resultSet2.getString("name"));
            }
        }
    }

    @Test
    public void statement() throws SQLException, ClassNotFoundException {
        final String url = "jdbc:postgresql://localhost:5432/postgres";
//        final String url = "jdbc:h2:mem:test_mem";
        final String username = "postgres";
        final String password = "postgres";

        Class.forName("org.postgresql.Driver");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            final Statement statement = connection.createStatement();
            statement.execute("DROP SCHEMA IF EXISTS unit8schema CASCADE ;");

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

    private void insertAuthorBad(int id, String name, Connection connection) throws SQLException {
        final Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO unit8schema.author(id, name) VALUES (" + id + ", '" + name + "');");
    }

    private void insertAuthor(int id, String name, Connection connection) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO unit8schema.author(id, name) VALUES (?, ?);"
        );

        statement.setInt(1, id);
        statement.setString(2, name);

        statement.executeUpdate();
    }
    @Test
    public void statement2() throws SQLException, ClassNotFoundException {
        final String url = "jdbc:postgresql://localhost:5432/postgres";
//        final String url = "jdbc:h2:mem:test_mem";
        final String username = "postgres";
        final String password = "postgres";

        Class.forName("org.postgresql.Driver");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            final Statement statement = connection.createStatement();
            statement.execute("DROP SCHEMA IF EXISTS unit8schema CASCADE ;");

            statement.execute("CREATE SCHEMA unit8schema;");

            statement.execute(
                    "CREATE TABLE unit8schema.author ( id INTEGER PRIMARY KEY, name VARCHAR(255) NOT NULL);"
            );

            insertAuthor(1, "Терри Пратчетт", connection);
            insertAuthor(2, "Иммануил Кант", connection);
            insertAuthor(3, "Иммануил Кант'); DELETE FROM unit8schema.author; INSERT INTO unit8schema.author values (1, 'HA-HA-HA", connection);

            final ResultSet resultSet = statement.executeQuery("SELECT id, name FROM unit8schema.author");

            while (resultSet.next()) {
                System.out.printf("Author[id: %d, name: %s]\n", resultSet.getInt("id"), resultSet.getString("name"));
            }
        }
    }

    private int insertAuthorReturning(String name, Connection connection) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO unit8schema.author(id, name) VALUES (nextval('unit8schema.author_seq'), ?);",
                new String[]{"id"}
        );

        statement.setString(1, name);

        statement.executeUpdate();

        final ResultSet keys = statement.getGeneratedKeys();
        keys.next();
        return keys.getInt(1);
//        final int cnt = keys.getMetaData().getColumnCount();
//        while (keys.next()) {
//            for (int i = 1; i <= cnt; i++) {
//                System.out.print(keys.getObject(i));
//                System.out.print(", ");
//            }
//            System.out.println();
//        }

    }

    @Test
    public void statementReturning() throws SQLException, ClassNotFoundException {
        final String url = "jdbc:postgresql://localhost:5432/postgres";
//        final String url = "jdbc:h2:mem:test_mem";
        final String username = "postgres";
        final String password = "postgres";

        Class.forName("org.postgresql.Driver");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            final Statement statement = connection.createStatement();
            statement.execute("DROP SCHEMA IF EXISTS unit8schema CASCADE ;");

            statement.execute("CREATE SCHEMA unit8schema;");

            statement.execute(
                    "CREATE TABLE unit8schema.author ( id INTEGER PRIMARY KEY, name VARCHAR(255) NOT NULL);" +
                            "CREATE SEQUENCE unit8schema.author_seq;"
            );

            final int id1 =insertAuthorReturning("Терри Пратчетт", connection);
            final int id2 = insertAuthorReturning("Иммануил Кант", connection);
            final int id3 =
                    insertAuthorReturning("Иммануил Кант'); DELETE FROM unit8schema.author; INSERT INTO unit8schema.author values (1, 'HA-HA-HA", connection);

            System.out.println(id1 + ", " + id2 + ", " + id3);

            final ResultSet resultSet = statement.executeQuery("SELECT id, name FROM unit8schema.author");

            while (resultSet.next()) {
                System.out.printf("Author[id: %d, name: %s]\n", resultSet.getInt("id"), resultSet.getString("name"));
            }
        }
    }

}
