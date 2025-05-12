package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmployeeDBTest {
  private Connection connection;

  @BeforeEach
  void setUp() throws SQLException {
    connection = EmployeeDB.connectDatabase();
  }

  @AfterEach
  void tearDown() throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute("DROP TABLE IF EXISTS employees");
    }
    connection.close();
  }

  @Test
  void testSaveToDatabase() throws SQLException {
    // Execute the method being tested (Action)
    EmployeeDB.saveToDatabase();

    // Verify the results (Assertions)
    try (Statement stmt = connection.createStatement()) {
      ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM employees");
      rs.next();
      assertEquals(5, rs.getInt("total"));

      rs = stmt.executeQuery("SELECT * FROM employees WHERE name = 'John'");
      rs.next();
      assertEquals("John", rs.getString("name"));
    }
  }

  @Test
  void testReadFromDatabase() throws SQLException {
    // Execute the method being tested (Action)
    EmployeeDB.saveToDatabase();
    EmployeeDB.readFromDatabase();

    // Verify the results (Assertions)
    try (Statement stmt = connection.createStatement()) {
      ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM employees");
      rs.next();
      assertEquals(5, rs.getInt("total"));
    }
  }
}
