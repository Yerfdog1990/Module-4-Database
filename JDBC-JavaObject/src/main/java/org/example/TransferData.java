package org.example;

import java.sql.*;

public class TransferData {

  // Save class objects to a database
  public static void saveToDatabase(SchoolDB schoolDB, School school) throws SQLException {
    // Create a table in the database
    try (Connection connection = schoolDB.connectToDatabase()) {
      String sql =
          "CREATE TABLE IF NOT EXISTS schools (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), city VARCHAR(255))";
      Statement statement = connection.createStatement();
      statement.executeUpdate(sql);
      // Insert class objects into the database
      PreparedStatement preparedStatement =
          connection.prepareStatement("INSERT INTO schools (name, city) VALUES (?, ?)");
      preparedStatement.setString(1, school.getName());
      preparedStatement.setString(2, school.getCity());
      preparedStatement.executeUpdate();
      System.out.println("Data saved to database");
      // Print the results of the query
      preparedStatement = connection.prepareStatement("SELECT * FROM schools");
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        System.out.println("ID: " + resultSet.getInt("id"));
        System.out.println("Name: " + resultSet.getString("name"));
        System.out.println("City: " + resultSet.getString("city"));
      } else {
        throw new SQLException("No data found");
      }
      statement.close();
      preparedStatement.close();
    }
  }

  // Read class objects from a database
  public static School readFromDatabaseById(SchoolDB schoolDB) throws SQLException {
    School school = null;
    try (Connection connection = schoolDB.connectToDatabase()) {
      PreparedStatement preparedStatement =
          connection.prepareStatement("SELECT * FROM schools WHERE id = ?");
      preparedStatement.setInt(1, 1);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        school =
            new School(
                resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("city"));
        System.out.println("\nData read from database");
        System.out.println("ID: " + resultSet.getInt("id"));
        System.out.println("Name: " + resultSet.getString("name"));
        System.out.println("City: " + resultSet.getString("city"));
        preparedStatement.close();
        resultSet.close();
      } else {
        throw new SQLException("No data found");
      }
      return school;
    }
  }
}
