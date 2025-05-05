package org.jdbcutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HandleNullValues {
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
          statement.executeUpdate(
              "INSERT INTO students (name, gender, gpa, birthday) VALUES ('Michael', 'Male', 3.8, '1999-09-09')");
          statement.executeUpdate(
              "INSERT INTO students (name, gender, gpa, birthday) VALUES ('George', 'Female', 3.7, '1998-08-08')");

          ResultSet resultSet = statement.executeQuery("SELECT * FROM students");
          while (resultSet.next()) {
            String name = resultSet.getString("name");
            String gender = resultSet.getString("gender");
            double gpa = resultSet.getDouble("gpa");
            String birthday = resultSet.getString("birthday");
            assert name != null;
            System.out.println(name + ", " + gender + ", " + gpa + ", " + birthday);

            // Resting different SQL methods
            System.out.println("Is cursor on the first row? " + resultSet.isFirst());
            System.out.println("Is cursor on the last row? " + resultSet.isLast());
            System.out.println("Is cursor before the first row? " + resultSet.isBeforeFirst());
            System.out.println("Is cursor after the last row? " + resultSet.isAfterLast());
            System.out.println("Row number: " + resultSet.getRow());
            System.out.println("Column count: " + resultSet.getMetaData().getColumnCount());
            System.out.println(resultSet.getMetaData().getColumnLabel(1));
            System.out.println(resultSet.getMetaData().getColumnLabel(2));
            System.out.println(resultSet.getMetaData().getColumnLabel(3));
            System.out.println(resultSet.getMetaData().getColumnLabel(4));
            System.out.println("Current row: " + resultSet.getObject(1));
            System.out.println("Current row: " + resultSet.getObject(2));
            System.out.println("Current row: " + resultSet.getObject(3));
            System.out.println("Current row: " + resultSet.getObject(4));
            System.out.println("Relative: " + resultSet.relative(1));
          }
        };
    JDBCUtils.doWithStatement(consumerStatement);
  }
}
