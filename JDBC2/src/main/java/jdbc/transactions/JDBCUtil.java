package jdbc.transactions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtil {
  private final Connection connection;

  // Constructor
  private JDBCUtil(Connection connection) {
    this.connection = connection;
  }

  // Method to create get connection
  public static Connection getConnection() throws SQLException {
    String connectionUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    String username = "USER";
    String password = "<PASSWORD>";
    return DriverManager.getConnection(connectionUrl, username, password);
  }

  // Method to create a connection
  public static void standAloneRun(MyConsumer consumer) throws SQLException {
    try (Connection connection = getConnection()) {
      consumer.accept(connection);
    }
  }

  public static void standAloneRunWithTransaction(MyConsumer consumer) throws SQLException {
    runWithTransaction(null, consumer);
  }

  // Method to create a transaction
  public static void runWithTransaction(Connection connection, MyConsumer consumer)
      throws SQLException {
    if (connection == null) connection = getConnection();
    try {
      connection = getConnection();
      // Begin transaction
      connection.setAutoCommit(false);
      // Doing transactional work
      consumer.accept(connection);
      // Commit
      connection.commit();
    } catch (SQLException e) {
      if (connection != null) {
        connection.rollback();
        System.out.println("\nTransaction rolled back");
        connection.close();
      }
      throw new SQLException(e);
    }
  }

  public void runWithTransaction(MyConsumer consumer) {
    try {
      runWithTransaction(connection, consumer);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
