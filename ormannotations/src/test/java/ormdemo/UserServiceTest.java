package ormdemo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTest {
  private UserService userService;

  @BeforeEach
  void setUp() throws SQLException {
    userService = new UserService();
  }

  @Test
  void testAuthenticatePass() throws SQLException {
    String username = "user1";
    String password = "password1";
    boolean authenticated = userService.authenticate(username, password);
    assertTrue(authenticated);
  }

  @Test
  void testAuthenticateFail() throws SQLException {
    String username = "user1";
    String password = "wrongpassword";
    boolean authenticated = userService.authenticate(username, password);
    assertFalse(authenticated);
  }
}
