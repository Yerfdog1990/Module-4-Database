package ormdemo;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TDDBaseUserAuthenticationLogicTest {
  private static IUserService userService;

  @BeforeAll
  static void setUp() throws SQLException {
    UserRepository userRepository = Mockito.mock(UserRepository.class);

    // Create mock users
    User user1 = new User();
    user1.setUsername("user1");
    user1.setPassword("password1");
    user1.setActive(true);

    User user2 = new User();
    user2.setUsername("user2");
    user2.setPassword("password2");
    user2.setActive(true);

    // Mock getUser method instead of getPassword
    Mockito.when(userRepository.getUser("user1")).thenReturn(user1);
    Mockito.when(userRepository.getUser("user2")).thenReturn(user2);

    userService = new UserServiceImpl(userRepository, new NoopPasswordEncoder());
  }

  @Test
  void loginTestPass() throws SQLException {
    String username = "user1";
    String password = "password1";
    boolean authenticated = userService.authenticate(username, password);
    assertTrue(authenticated);
  }

  @Test
  void loginTestFail() throws SQLException {
    String username = "user1";
    String password = "password";
    boolean authenticated = userService.authenticate(username, password);
    assertFalse(authenticated);
  }
}
