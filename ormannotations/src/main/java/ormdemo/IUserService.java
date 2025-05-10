package ormdemo;

import java.sql.SQLException;

public interface IUserService {
  boolean authenticate(String username, String password) throws SQLException;
}
