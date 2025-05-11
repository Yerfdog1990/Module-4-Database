package org.example;

import java.sql.*;

public class SchoolDB {
  // Setup database connection
  public static Connection connectToDatabase() throws SQLException {
    String url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    String user = "sa";
    String password = "";
    return DriverManager.getConnection(url, user, password);
  }
}
