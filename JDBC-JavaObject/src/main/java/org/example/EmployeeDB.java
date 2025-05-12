package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

public class EmployeeDB {

  public static Connection connectDatabase() throws SQLException {
    String url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    String user = "sa";
    String password = "";
    return DriverManager.getConnection(url, user, password);
  }

  public static void saveToDatabase() throws SQLException {
    // Create a set of employee objects
    Set<Employee> employees =
        Set.of(
            new Employee(1, "John", "CEO", 100000),
            new Employee(2, "Jane", "Manager", 50000),
            new Employee(3, "Mike", "Engineer", 25000),
            new Employee(4, "Tom", "Intern", 10000),
            new Employee(5, "Jerry", "Intern", 10000));
    // Create a table to add employee objects
    try (Connection connection = connectDatabase()) {
      String sql =
          "CREATE TABLE IF NOT EXISTS employees (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), position VARCHAR(255), salary INTEGER)";
      java.sql.Statement statement = connection.createStatement();
      statement.executeUpdate(sql);
      // Add employee objects to the database
      PreparedStatement preparedStatement =
          connection.prepareStatement(
              "INSERT INTO employees (name, position, salary) VALUES (?, ?, ?)");
      for (Employee employee : employees) {
        preparedStatement.setString(1, employee.getName());
        preparedStatement.setString(2, employee.getPosition());
        preparedStatement.setInt(3, employee.getSalary());
        preparedStatement.addBatch();
        preparedStatement.executeBatch();
        preparedStatement.clearBatch();
      }
    }
  }

  public static void readFromDatabase() throws SQLException {
    // Read employee objects from the database
    try (Connection connection = connectDatabase()) {
      java.sql.Statement statement = connection.createStatement();
      statement.execute("SELECT * FROM employees");
      java.sql.ResultSet resultSet = statement.getResultSet();
      while (resultSet.next()) {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String position = resultSet.getString("position");
        int salary = resultSet.getInt("salary");
        System.out.println(
            "ID: " + id + ", Name: " + name + ", Position: " + position + ", Salary: " + salary);
      }
    }
  }

  // Main method
  public static void main(String[] args) throws SQLException {
    saveToDatabase();
    readFromDatabase();
  }
}
