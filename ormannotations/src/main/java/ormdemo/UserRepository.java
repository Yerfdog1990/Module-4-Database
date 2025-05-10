package ormdemo;

import java.sql.SQLException;

public interface UserRepository {
  String getPassword(String username) throws SQLException;

  User getUser(String username) throws SQLException;
}
