package com.epam.course.java.se.account;

import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public abstract class AbstractAccountsTest {
    abstract DataSource getDataSource();

    private void transfer(int account1, int account2, int delta) throws SQLException, InterruptedException {
        try (Connection connection = getDataSource().getConnection()) {
            connection.setAutoCommit(false);

            final PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, amount" +
                            " FROM account" +
                            " WHERE id = ?" +
                            " FOR UPDATE",
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_UPDATABLE
            );

            statement.setInt(1, account1);

            final ResultSet resultSet1 = statement.executeQuery();

            resultSet1.next();

            resultSet1.updateInt("amount", resultSet1.getInt("amount") - delta);
            resultSet1.updateRow();

            System.out.printf("From %d to %d step1\n", account1, account2);

            Thread.sleep(10);

            System.out.printf("From %d to %d step1.1\n", account1, account2);

            statement.setInt(1, account2);

            final ResultSet resultSet2 = statement.executeQuery();

            resultSet2.next();

            resultSet2.updateInt("amount", resultSet2.getInt("amount") + delta);
            resultSet2.updateRow();

            System.out.printf("From %d to %d step2\n", account1, account2);

            connection.commit();
        }
    }

    private void changeAmount(int id, int delta, int delay) throws SQLException, InterruptedException {
        try (Connection connection = getDataSource().getConnection()) {
            connection.setAutoCommit(false);
//            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            final PreparedStatement statement = connection
                    .prepareStatement(
                            "SELECT id, amount" +
                                    " FROM account" +
                                    " WHERE id = ?" +
                                    " FOR UPDATE",
                            ResultSet.TYPE_FORWARD_ONLY,
                            ResultSet.CONCUR_UPDATABLE
                    );

            statement.setInt(1, id);

            final ResultSet resultSet = statement.executeQuery();

            resultSet.next();

            final int amount = resultSet.getInt("amount");
            System.out.println("Got amount " + amount + ", delta: " + delta);
            Thread.sleep(delay);

            resultSet.updateInt("amount", amount + delta);
            resultSet.updateRow();
            connection.commit();
            System.out.println("Updated amount " + amount + ", delta: " + delta);
        }
    }

    @Test
    public void test() throws SQLException, InterruptedException {
        try (Connection connection = getDataSource().getConnection()) {
            connection
                    .createStatement()
                    .execute(
                            "CREATE TABLE account(id INTEGER PRIMARY KEY, amount INTEGER NOT NULL);" +
                                    " INSERT INTO account(id, amount)" +
                                    " SELECT 1, 0;"
                    );
        }

        final Thread increase = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 2; i++) {
                    try {
                        changeAmount(1, 50, 500);
                        Thread.sleep(10);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        final Thread decrease = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        changeAmount(1, -10, 1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


        decrease.start();
        increase.start();

        increase.join();
        decrease.join();

        try (Connection connection = getDataSource().getConnection()) {
            final ResultSet resultSet = connection
                    .createStatement()
                    .executeQuery(
                            "SELECT id, amount" +
                                    " FROM account"
                    );

            while (resultSet.next()) {
                System.out.printf(
                        "account[id: %s, amount: %s]\n",
                        resultSet.getInt("id"),
                        resultSet.getInt("amount")
                );
            }

            connection
                    .createStatement()
                    .execute(
                            "DROP TABLE account;"
                    );
        }

    }

    @Test
    public void test2() throws SQLException, InterruptedException {
        try (Connection connection = getDataSource().getConnection()) {
            connection
                    .createStatement()
                    .execute(
                            "CREATE TABLE account(id INTEGER PRIMARY KEY, amount INTEGER NOT NULL);" +
                                    " INSERT INTO account(id, amount)" +
                                    " SELECT 1, 0" +
                                    " UNION ALL SELECT 2, 0;"
                    );
        }

        final Thread increase = new Thread() {
            @Override
            public void run() {
                try {
                    transfer(1, 2, 100);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        final Thread decrease = new Thread() {
            @Override
            public void run() {
                try {
                    transfer(2, 1, 100);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };


        decrease.start();
        increase.start();

        increase.join();
        decrease.join();

        try (Connection connection = getDataSource().getConnection()) {
            final ResultSet resultSet = connection
                    .createStatement()
                    .executeQuery(
                            "SELECT cast(something.id AS DECIMAL(12, 3)) AS some_id, something.amount, 'x' AS const FROM account AS something"
                    );

            printResultSet(resultSet);

            final DatabaseMetaData databaseMetaData = connection.getMetaData();
            System.out.println("ProductName: " + databaseMetaData.getDatabaseProductName());
            System.out.println("ProductVersion: " + databaseMetaData.getDatabaseProductVersion());
            System.out.println("DriverName: " + databaseMetaData.getDriverName());
            System.out.println("DriverVersion: " + databaseMetaData.getDriverVersion());
            System.out.println("UserName: " + databaseMetaData.getUserName());
            System.out.println("URL: " + databaseMetaData.getURL());

            final ResultSet tables = databaseMetaData.getTables(null, null, null, null);

            printResultSet(tables);

        }

    }

    private void printResultSet(ResultSet resultSet) throws SQLException {
        final ResultSetMetaData metaData = resultSet.getMetaData();

        while (resultSet.next()) {
            final Map<String, StringJoiner> map = new LinkedHashMap<>();

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                final StringJoiner joiner =
                        map.computeIfAbsent(metaData.getTableName(i), tn -> new StringJoiner(", ", tn + "[", "]"));
                final String element =
                        String.format(
                                "%s(%d, %s(%d, %d)): %s",
                                metaData.getColumnName(i),
                                i,
                                metaData.getColumnTypeName(i),
                                metaData.getPrecision(i),
                                metaData.getScale(i),
                                Objects.toString(resultSet.getObject(i), "<null>"));
                joiner.add(element);
            }

            final StringJoiner joiner = new StringJoiner("; ", "Tables: ", "");

            for (StringJoiner elem : map.values()) {
                joiner.add(elem.toString());
            }

            System.out.println(joiner.toString());
        }
    }


}
