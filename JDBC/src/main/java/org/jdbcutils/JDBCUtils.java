package org.jdbcutils;

import java.sql.*;
import java.util.function.Consumer;

public class JDBCUtils {
  private static Connection createConnection() throws SQLException {
    String connectionUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    String username = "root";
    String password = "<PASSWORD>";
    return DriverManager.getConnection(connectionUrl, username, password);
  }

  public static void doWithConnection(Consumer<Connection> action) throws SQLException {
    try (Connection connection = createConnection()) {
      action.accept(connection);
    }
  }

  public static void doWithStatement(MyConsumer<Statement> action) throws SQLException {
    doWithConnection(
        connection -> {
          try (Statement statement = connection.createStatement()) {
            action.accept(statement);
          } catch (SQLException e) {
            e.getMessage();
          }
        });
  }

  // Main method
  public static void main(String[] args) throws SQLException {
    MyConsumer<Statement> consumerStatement =
        statement -> {
          statement.executeUpdate(
              "CREATE TABLE IF NOT EXISTS students (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), gender ENUM('Male', 'Female', 'Other'), gpa DECIMAL, birthday DATE)");
          statement.executeUpdate(
              "INSERT INTO students (name, gender, gpa, birthday) VALUES ('John', 'Male', 3.8, '1999-09-09')");
          statement.executeUpdate(
              "INSERT INTO students (name, gender, gpa, birthday) VALUES ('Jane', 'Female', 3.7, '1998-08-08')");
          statement.executeUpdate(
              "INSERT INTO students (name, gender, gpa, birthday) VALUES ('Mary', 'Other', 3.1, '1997-07-07')");
          statement.executeUpdate(
              "INSERT INTO students (name, gender, gpa, birthday) VALUES ('Mike', 'Male', 2.5, '1996-06-06')");
          statement.executeUpdate(
              "INSERT INTO students (name, gender, gpa, birthday) VALUES ('Anna', 'Female', 3.6, '1995-05-05')");

          ResultSet resultSet = statement.executeQuery("SELECT * FROM students");
          while (resultSet.next()) {
            String name = resultSet.getString("name");
            String gender = resultSet.getString("gender");
            double gpa = resultSet.getDouble("gpa");
            String birthday = resultSet.getString("birthday");
            System.out.println(name + ", " + gender + ", " + gpa + ", " + birthday);
          }
        };
    doWithStatement(consumerStatement);
  }
}
