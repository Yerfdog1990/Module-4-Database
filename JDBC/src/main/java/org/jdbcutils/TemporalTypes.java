package org.jdbcutils;

import static java.lang.System.currentTimeMillis;

import java.sql.*;

public class TemporalTypes {
  public static void main(String[] args) throws SQLException {
    MyConsumer<Statement> consumerStatement =
        statement -> {
          try {
            String creatTableSQL =
                "CREATE TABLE events (id INT PRIMARY KEY AUTO_INCREMENT, date DATE, time TIME, timestamp TIMESTAMP)";
            statement.execute(creatTableSQL);
          } catch (SQLException e) {
            System.err.println("Error executing SQL: " + e.getMessage());
            throw e;
          }
        };
    JDBCUtils.doWithConnection(
        connection -> {
          // Create table
          try (Statement statement = connection.createStatement()) {
            consumerStatement.accept(statement);
          } catch (SQLException e) {
            System.err.println("Error creating or executing statement: " + e.getMessage());
          }

          // Insert data
          try (PreparedStatement preparedStatement =
              connection.prepareStatement(
                  "INSERT INTO events (id, date, time, timestamp) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setInt(1, 1);
            preparedStatement.setDate(2, new Date(currentTimeMillis()));
            preparedStatement.setTime(3, new Time(currentTimeMillis()));
            preparedStatement.setTimestamp(4, new Timestamp(currentTimeMillis()));
            preparedStatement.executeUpdate();

            // Print content of the table
            Statement selectStatement = connection.createStatement();
            ResultSet resultSet = selectStatement.executeQuery("SELECT * FROM events");
            while (resultSet.next()) {
              System.out.println(
                  resultSet.getInt("id")
                      + ", "
                      + resultSet.getDate("date")
                      + ", "
                      + resultSet.getTime("time")
                      + ", "
                      + resultSet.getTimestamp("timestamp"));
            }
          } catch (SQLException e) {
            System.err.println("Error preparing or executing statement: " + e.getMessage());
          }
        });
  }
}
