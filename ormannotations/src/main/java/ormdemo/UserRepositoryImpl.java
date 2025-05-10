package ormdemo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepositoryImpl implements UserRepository {
  private final Connection connection;

  public UserRepositoryImpl(Connection connection) {
    this.connection = connection;
  }

  @Override
  public String getPassword(String username) throws SQLException {
    if (username == null) throw new IllegalArgumentException("Invalid input");
    PreparedStatement statement =
        connection.prepareStatement("SELECT PASSWORD FROM TEST WHERE USERS = ?");
    statement.setString(1, username);
    ResultSet resultSet = statement.executeQuery();
    if (resultSet.next()) {
      return resultSet.getString("password");
    }
    return null;
  }

  @Override
  public User getUser(String userName) throws SQLException {
    if (userName == null) throw new IllegalArgumentException("Invalid input");
    PreparedStatement statement =
        connection.prepareStatement("SELECT PASSWORD, is_Active FROM TEST WHERE USERS = ?");
    statement.setString(1, userName);
    ResultSet resultSet = statement.executeQuery();
    if (resultSet.next()) {
      boolean isActive = resultSet.getBoolean("is_Active");
      String storedPassword = resultSet.getString("password");
      User user = new User();
      user.setUsername(userName);
      user.setPassword(storedPassword);
      user.setActive(isActive);
      return user;
    }
    return null;
  }
}
