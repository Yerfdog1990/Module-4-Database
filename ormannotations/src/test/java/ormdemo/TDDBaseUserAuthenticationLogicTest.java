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
    Mockito.when(userRepository.getPassword("user1")).thenReturn("password1");
    Mockito.when(userRepository.getPassword("user2")).thenReturn("password2");
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
