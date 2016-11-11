package com.epam.course.java.se.accountsbkp;

import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;

public abstract class AbstractAccountsTest {
    abstract DataSource getDataSource();

    private void transfer(int account1, int account2, int amount) {

    }

    private void changeAmount(int account, int delta, int delay) throws SQLException, InterruptedException {
        try (Connection connection = getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            final PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, amount" +
                            " FROM account" +
                            " WHERE id = ?" +
                            " FOR UPDATE ",
//                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_UPDATABLE
            );

            statement.setInt(1, account);

            final ResultSet resultSet = statement.executeQuery();

            resultSet.next();

            final int amount = resultSet.getInt("amount");
            System.out.println("Got amount " + amount + ", delta: " + delta);
            Thread.sleep(delay);
            resultSet.updateInt("amount", amount + delta);
            resultSet.updateRow();
            System.out.println("Updated amount " + amount + ", delta: " + delta);
            connection.commit();
        }
    }

    @Test
    public void test1() throws SQLException, InterruptedException {
        try (Connection connection = getDataSource().getConnection()) {
            final Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE account(id INTEGER PRIMARY KEY, amount INTEGER);" +
                    "INSERT INTO account(id, amount)" +
                    " SELECT 1, 1000000" +
                    " UNION ALL SELECT 2, 1000000");
        }

        final Thread increase = new Thread() {
            @Override
            public void run() {
                try {
                    changeAmount(1, 100, 1000);
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

        increase.start();
        decrease.start();

        increase.join();
        decrease.join();

        try (Connection connection = getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            final ResultSet resultSet = connection
                    .createStatement()
                    .executeQuery(
                            "SELECT id, amount" +
                                    " FROM account" +
                                    " FOR UPDATE"
                    );

            while (resultSet.next()) {
                System.out.println(
                        "account[id: " + resultSet.getInt("id") + ", name: " + resultSet.getInt("amount") + "]"
                );
            }
            connection.commit();
        }
    }
}
