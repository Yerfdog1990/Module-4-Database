package ormdemo;

import java.sql.*;

public class UserService {
  private final Connection connection;

  public UserService() throws SQLException {
    // Load the JDBC driver
    connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    // Create a table
    Statement statement = connection.createStatement();
    // Drop the table if it exists and recreate it
    statement.execute("DROP TABLE IF EXISTS TEST");
    statement.execute(
        "CREATE TABLE IF NOT EXISTS TEST(ID INT PRIMARY KEY, USERS VARCHAR(255) UNIQUE NOT NULL, PASSWORD VARCHAR(255), is_Active BIT)");
    // Insert two users
    statement.execute("INSERT INTO TEST VALUES(1, 'user1', 'password1', 1)");
    statement.execute("INSERT INTO TEST VALUES(2, 'user2', 'password2', 0)");
  }

  public boolean authenticate(String userName, String password) throws SQLException {
    // Check if the username and password are null
    if (userName == null || password == null) throw new IllegalArgumentException("Invalid input");
    // Create a prepared statement and retrieve the USERS using their name and password
    PreparedStatement statement =
        connection.prepareStatement(
            "SELECT USERS, PASSWORD FROM TEST WHERE USERS = ? AND PASSWORD = ?");
    statement.setString(1, userName);
    statement.setString(2, password);
    // Execute the query and retrieve the result set
    ResultSet resultSet = statement.executeQuery();
    // Get the result before closing resources
    try {
      if (resultSet.next()) {
        String userPassword = resultSet.getString("password");
        return password.equals(userPassword);
      }
      return false;
    } finally {
      // Close the statement and result set
      resultSet.close();
      statement.close();
    }
  }
}
