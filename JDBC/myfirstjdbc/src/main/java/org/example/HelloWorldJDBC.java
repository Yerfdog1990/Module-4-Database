package org.example;

import java.sql.*;

public class HelloWorldJDBC {
  public static void main(String[] args) throws SQLException {
    String connectionUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    String userName = "yerfdog";
    String password = "root";
    try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password)) {
      try (Statement statement = connection.createStatement()) {
        statement.execute(
            "CREATE TABLE students (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, gender ENUM('male', 'female', 'other') NOT NULL, birthday DATE NOT NULL, gpa DECIMAL(3,2) CHECK (gpa >= 0.00 AND gpa <= 4.00))");
        statement.execute(
            "INSERT INTO students (name, gender, birthday, gpa) VALUES ('John Smith', 'male', '2000-05-15', 3.75)");
        statement.execute(
            "INSERT INTO students (name, gender, birthday, gpa) VALUES ('Emma Johnson', 'female', '2001-03-22', 4.00)");
        statement.execute(
            "INSERT INTO students (name, gender, birthday, gpa) VALUES ('Alex Chen', 'other', '2000-11-30', 3.85)");
        statement.execute(
            "INSERT INTO students (name, gender, birthday, gpa) VALUES ('Sarah Davis', 'female', '2001-07-18', 3.50)");
        statement.execute(
            "INSERT INTO students (name, gender, birthday, gpa) VALUES ('Michael Brown', 'male', '2000-09-25', 3.25)");
        statement.execute(
            "INSERT INTO students (name, gender, birthday, gpa) VALUES ('Lisa Wilson', 'female', '2001-01-12', 3.90)");
        statement.execute(
            "INSERT INTO students (name, gender, birthday, gpa) VALUES ('James Taylor', 'male', '2000-04-08', 3.60)");
        statement.execute(
            "INSERT INTO students (name, gender, birthday, gpa) VALUES ('Emily White', 'female', '2001-08-29', 3.95)");
        statement.execute(
            "INSERT INTO students (name, gender, birthday, gpa) VALUES ('Sam Martinez', 'other', '2000-12-05', 3.70)");
        statement.execute(
            "INSERT INTO students (name, gender, birthday, gpa) VALUES ('David Lee', 'male', '2001-06-20', 3.80)");
        statement.execute("SELECT * FROM STUDENTS");

        ResultSet resultSet = statement.executeQuery("SELECT * FROM STUDENTS");
        System.out.println("| ID | Name | Gender | Birthday | GPA |");
        while (resultSet.next()) {
          int id = resultSet.getInt("id");
          String name = resultSet.getString("name");
          String gender = resultSet.getString("gender");
          Date birthday = resultSet.getDate("birthday");
          double gpa = resultSet.getDouble("gpa");
          System.out.println(
              "| " + id + " | " + name + " |  " + gender + " |  " + birthday + " |  " + gpa + "");
        }
      }
    }
  }
}
