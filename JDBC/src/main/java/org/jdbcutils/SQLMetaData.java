package org.jdbcutils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLMetaData {
  public static void main(String[] args) throws SQLException {
    MyConsumer<Statement> statementConsumer =
        statement -> {
          statement.execute(
              "CREATE TABLE students (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), gender ENUM('Male', 'Female', 'Other'), gpa DECIMAL, birthday DATE)");
          statement.execute(
              "INSERT INTO students (name, gender, gpa, birthday) VALUES ('John', 'Male', 3.8, '1999-09-09')");
          statement.execute(
              "INSERT INTO students (name, gender, gpa, birthday) VALUES ('Jane', 'Female', 3.7, '1998-08-08')");
          statement.execute(
              "INSERT INTO students (name, gender, gpa, birthday) VALUES ('Mary', 'Other', 3.1, '1997-07-07')");
          statement.execute(
              "INSERT INTO students (name, gender, gpa, birthday) VALUES ('Mike', 'Male', 2.5, '1996-06-06')");

          ResultSet resultSet = statement.executeQuery("SELECT * FROM students");
          ResultSetMetaData metaData = resultSet.getMetaData();
          int columnCount = metaData.getColumnCount();
          for (int i = 1; i <= columnCount; i++) {
            System.out.printf(
                "%s - %s - %s - %s\n",
                metaData.getColumnName(i),
                metaData.getColumnTypeName(i),
                metaData.getColumnCount(),
                metaData.isAutoIncrement(i));
          }
        };
    JDBCUtils.doWithStatement(statementConsumer);
  }
}
