package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BankSimulator {
    private final Connection connection;

    public BankSimulator(Connection connection) {
        this.connection = connection;
    }

    // Method to set up the database
    public static Connection getConnection() throws SQLException {
        String connectionUrl = "h2:mem:testdb;DB_CLOSE_DELAY=-1";
        String username = "user";
        String password = "password";
        return DriverManager.getConnection(connectionUrl, username, password);
    }

    public static void standAloneRun(BankConsumer consumer) throws SQLException {
        try (Connection connection = getConnection()) {
            consumer.accept(connection);
        }
    }

    public static void standAloneRunWithTransaction(BankConsumer consumer) throws SQLException {
        runWithTransaction(consumer, null);
    }

    // Method to create a connection
    public static void runWithTransaction(BankConsumer consumer, Connection connection) throws SQLException {
        if (connection == null) {
            connection = getConnection();
            // Begin transaction
            connection.setAutoCommit(false);
            try {
                connection = getConnection();

                // Run transactions
                consumer.accept(connection);

                // Commit transaction
                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    // Rollback transaction
                    connection.rollback();
                    System.out.println("Transaction rolled back");
                    // Close connection
                    connection.close();
                }
                throw new SQLException("Transaction failed", e);
            }
        }
    }
}
