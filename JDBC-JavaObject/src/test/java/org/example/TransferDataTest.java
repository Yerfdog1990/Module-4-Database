package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransferDataTest {
  SchoolDB scheduleDB;
  School school;

  @BeforeEach
  void setUp() {
    SchoolDB schoolDB = new SchoolDB();
    School school = new School(1, "Test School", "Test City");
    this.scheduleDB = schoolDB;
    this.school = school;
  }

  @Test
  void testSaveToDatabase() throws SQLException {
    TransferData.saveToDatabase(scheduleDB, school);
    Connection connection = scheduleDB.connectToDatabase();
    Statement statement = connection.createStatement();
    statement.execute("SELECT * FROM schools WHERE name = 'Test School'");
    ResultSet resultSet = statement.getResultSet();
    resultSet.next();
    assertEquals(resultSet.getInt("id"), 1);
    assertEquals(resultSet.getString("name"), "Test School");
    assertEquals(resultSet.getString("city"), "Test City");
  }

  @Test
  void testReadFromDatabase() throws SQLException {
    TransferData.readFromDatabaseById(scheduleDB);
    Connection connection = scheduleDB.connectToDatabase();
    Statement statement = connection.createStatement();
    statement.execute("SELECT * FROM schools WHERE name = 'Test School'");
    ResultSet resultSet = statement.getResultSet();
    resultSet.next();
    assertEquals(resultSet.getInt("id"), 1);
    assertEquals(resultSet.getString("name"), "Test School");
  }
}
