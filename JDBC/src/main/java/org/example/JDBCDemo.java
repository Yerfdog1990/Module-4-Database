package org.example;

import java.sql.*;

public class JDBCDemo {
  public static void main(String[] args) throws SQLException {
    String username = "root";
    String password = "<PASSWORD>";
    String url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";

    try (Connection connection = DriverManager.getConnection(url, username, password)) {
      try (Statement statement = connection.createStatement()) {
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
        statement.executeUpdate(
            "INSERT INTO students (name, gender, gpa, birthday) VALUES ('Bob', 'Other', 1.4, '1994-04-04')");
        statement.executeUpdate(
            "INSERT INTO students (name, gender, gpa, birthday) VALUES ('Charlie', 'Male', 1.9, '1993-03-03')");
        statement.executeUpdate(
            "INSERT INTO students (name, gender, gpa, birthday) VALUES ('David', 'Female', 3.2, '1992-02-02')");
        statement.executeUpdate(
            "INSERT INTO students (name, gender, gpa, birthday) VALUES ('Eve', 'Other', 2.5, '1991-01-01')");
        statement.executeUpdate(
            "INSERT INTO students (name, gender, gpa, birthday) VALUES ('Frank', 'Male', 3.4, '1990-09-09')");
        statement.executeUpdate(
            "INSERT INTO students (name, gender, gpa, birthday) VALUES ('George', 'Female', 1.5, '1989-08-08')");
        ResultSet resultSet = statement.executeQuery("SELECT * FROM students");
        while (resultSet.next()) {
          String name = resultSet.getString("name");
          String gender = resultSet.getString("gender");
          double gpa = resultSet.getDouble("gpa");
          String birthday = resultSet.getString("birthday");
          System.out.println(name + ", " + gender + ", " + gpa + ", " + birthday);
        }
      }
    }
  }
}
