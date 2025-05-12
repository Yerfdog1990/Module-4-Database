package org.learning;

import java.sql.*;

public class ResultSetTypes {
  public static void main(String[] args) {
    String url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    String username = "sa";
    String password = "";

    try (Connection connection = DriverManager.getConnection(url, username, password)) {
      // Create and populate table
      createTestTable(connection);

      // Demonstrate different ResultSet types
      demonstrateForwardOnly(connection);
      demonstrateScrollInsensitive(connection);
      demonstrateScrollSensitive(connection);
      demonstrateUpdatable(connection);
      demonstrateHoldability(connection);
      demonstrateFetchDirections(connection);

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static void createTestTable(Connection connection) throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute(
          "CREATE TABLE IF NOT EXISTS employees ("
              + "id INT PRIMARY KEY AUTO_INCREMENT, "
              + "name VARCHAR(255), "
              + "salary DECIMAL(10,2))");

      stmt.execute(
          "INSERT INTO employees (name, salary) VALUES "
              + "('John Doe', 50000), "
              + "('Jane Smith', 60000), "
              + "('Bob Johnson', 55000)");
    }
  }

  private static void demonstrateForwardOnly(Connection connection) throws SQLException {
    try (Statement stmt =
        connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
      ResultSet rs = stmt.executeQuery("SELECT * FROM employees");
      System.out.println("\nForward-Only ResultSet:");
      while (rs.next()) {
        System.out.println(rs.getString("name") + ": $" + rs.getDouble("salary"));
      }
    }
  }

  private static void demonstrateScrollInsensitive(Connection connection) throws SQLException {
    try (Statement stmt =
        connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
      ResultSet rs = stmt.executeQuery("SELECT * FROM employees");
      System.out.println("\nScroll Insensitive ResultSet:");
      rs.afterLast();
      while (rs.previous()) {
        System.out.println(rs.getString("name") + ": $" + rs.getDouble("salary"));
      }
    }
  }

  private static void demonstrateScrollSensitive(Connection connection) throws SQLException {
    try (Statement stmt =
        connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
      ResultSet rs = stmt.executeQuery("SELECT * FROM employees");
      System.out.println("\nScroll Sensitive ResultSet:");
      rs.absolute(2); // Move to second row
      System.out.println("Second row: " + rs.getString("name"));
      rs.relative(-1); // Move one row back
      System.out.println("First row: " + rs.getString("name"));
    }
  }

  private static void demonstrateUpdatable(Connection connection) throws SQLException {
    try (Statement stmt =
        connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
      ResultSet rs = stmt.executeQuery("SELECT * FROM employees");
      System.out.println("\nUpdatable ResultSet:");
      while (rs.next()) {
        if (rs.getString("name").equals("John Doe")) {
          rs.updateDouble("salary", 52000);
          rs.updateRow();
          System.out.println("Updated John's salary");
        }
      }
    }
  }

  private static void demonstrateHoldability(Connection connection) throws SQLException {
    try (Statement stmt =
        connection.createStatement(
            ResultSet.TYPE_FORWARD_ONLY,
            ResultSet.CONCUR_READ_ONLY,
            ResultSet.HOLD_CURSORS_OVER_COMMIT)) {
      ResultSet rs = stmt.executeQuery("SELECT * FROM employees");
      System.out.println("\nHoldable ResultSet:");
      connection.commit(); // ResultSet remains open
      while (rs.next()) {
        System.out.println(rs.getString("name"));
      }
    }
  }

  private static void demonstrateFetchDirections(Connection connection) throws SQLException {
    try (Statement stmt =
        connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
      ResultSet rs = stmt.executeQuery("SELECT * FROM employees");
      System.out.println("\nFetch Direction Demo:");
      rs.setFetchDirection(ResultSet.FETCH_REVERSE);
      rs.afterLast();
      while (rs.previous()) {
        System.out.println(rs.getString("name"));
      }
    }
  }
}
