package com.epam.course.java.se.withdata;

import lombok.Builder;
import lombok.Data;
import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDbTest {
    private final Flyway flyway;

    abstract DataSource getDataSource();

    public AbstractDbTest() {
        this.flyway = new Flyway();
        this.flyway.setDataSource(getDataSource());
        this.flyway.setLocations("db/migrate");
    }

    @Before
    public void before() {
        flyway.migrate();
    }

    @After
    public void after() throws SQLException {
        flyway.clean();
        dropSchema();
    }

    protected abstract void dropSchema() throws SQLException;

    @Data
    @Builder
    static class ReservedBook {
        private final String bookName;
        private final String authorName;
        private final String readerName;
    }

    @Test
    public void test1() throws SQLException {
        try (Connection connection = getDataSource().getConnection()) {
            final int readerId = insertReader(connection, "Reader 1");

            takeBook(connection, readerId, "Корона пастуха");
            takeBook(connection, readerId, "Эгоистичный ген");

            final List<ReservedBook> allReservedBooks = getAllReservedBooks(connection);

            for (ReservedBook book : allReservedBooks) {
                System.out.println(book);
            }
        }
    }

    @Test
    public void test2() throws SQLException {
        try (Connection connection = getDataSource().getConnection()) {
            final int readerId = insertReader(connection, "Reader 2");

            takeBook(connection, readerId, "Бог как иллюзия");
            takeBook(connection, readerId, "Патриот");

            final List<ReservedBook> allReservedBooks = getAllReservedBooks(connection);

            for (ReservedBook book : allReservedBooks) {
                System.out.println(book);
            }
        }
    }

    private List<ReservedBook> getAllReservedBooks(Connection connection) throws SQLException {
        final PreparedStatement query = connection.prepareStatement(
                "SELECT b.name AS book_name," +
                        " r.name AS reader_name," +
                        " a.name AS author_name" +
                        " FROM unit8schema.book b" +
                        " JOIN unit8schema.reader r ON b.current_reader_id = r.id" +
                        " JOIN unit8schema.author a ON b.author_id = a.id"
        );

        final ResultSet resultSet = query.executeQuery();

        final List<ReservedBook> result = new ArrayList<>();
        while (resultSet.next()) {
            final ReservedBook reservedBook = ReservedBook.builder()
                    .bookName(resultSet.getString("book_name"))
                    .authorName(resultSet.getString("author_name"))
                    .readerName(resultSet.getString("reader_name"))
                    .build();
            result.add(reservedBook);
        }

        return result;
    }

    private void takeBook(Connection connection, int readerId, String bookName) throws SQLException {
        final PreparedStatement update = connection.prepareStatement(
                "UPDATE unit8schema.book" +
                        " SET current_reader_id = ?" +
                        " WHERE name = ?"
        );
        update.setInt(1, readerId);
        update.setString(2, bookName);
        update.executeUpdate();
    }

    private int insertReader(Connection connection, String reader) throws SQLException {
        final String[] returnColumns = {"id"};
        final PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO unit8schema.reader (id, name)" +
                        " VALUES (nextval('unit8schema.reader_seq'), ?);",
                returnColumns
        );
        insert.setString(1, reader);
        insert.executeUpdate();
        final ResultSet generatedKeys = insert.getGeneratedKeys();
        generatedKeys.next();
        return generatedKeys.getInt(1);
    }
}
