package jdbc.transactions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RegularTransactionExample {
    public static void main(String[] args) throws SQLException {
        // Begin transaction
        MyConsumer consumer =
                connection -> {
                    connection.setAutoCommit(false);
                    // Create the account table
                    Statement statement = connection.createStatement();
                    statement.execute("CREATE TABLE ACCOUNT (ID INT PRIMARY KEY, BALANCE DECIMAL(10,2))");

                    // Insert some data
                    statement.execute("INSERT INTO ACCOUNT VALUES (1, 500.00), (2, 200.00), (3, 800.00)");

                    // Transfer some amount between accounts with ID 1 and 2
                    try {
                        statement.execute("UPDATE ACCOUNT SET BALANCE = BALANCE - 100 WHERE ID = 1");
                        statement.execute("UPDATE ACCOUNT SET BALANCE = BALANCE + 100 WHERE ID = 2");
                        // simulateDBError();
                        // Commit transaction
                        connection.commit();
                        System.out.println("Transaction successful");
                        // print balance to stdout
                        printBalance();
                    } catch (SQLException e) {
                        System.out.println("Transaction failed");
                        connection.rollback();
                        printBalance();
                        throw new SQLException(e);
                    }
                };
        JDBCUtil.standAloneRunWithTransaction(consumer);
    }

    private static void simulateDBError() throws SQLException {
        throw new SQLException("Transaction failed");
    }

    private static void printBalance() throws SQLException {
        Connection connection = JDBCUtil.getConnection();
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM ACCOUNT");
        while (resultSet.next()) {
            System.out.println(resultSet.getInt(1) + "->" + resultSet.getBigDecimal(2));
        }
    }
}
