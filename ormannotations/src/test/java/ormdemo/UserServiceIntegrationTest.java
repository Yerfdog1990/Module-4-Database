package ormdemo;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceIntegrationTest {
  private IUserService userService;
  private Connection connection;

  @BeforeEach
  void setUp() throws SQLException {
    // Initialize the database connection
    connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    // Initialize the actual implementation
    UserRepository userRepository = new UserRepositoryImpl(connection);
    PasswordEncoder passwordEncoder = new NoopPasswordEncoder();
    userService = new UserServiceImpl(userRepository, passwordEncoder);

    // Set up test data
    connection.createStatement().execute("DROP TABLE IF EXISTS TEST");
    connection.createStatement().execute(
            "CREATE TABLE IF NOT EXISTS TEST(ID INT PRIMARY KEY, USERS VARCHAR(255) UNIQUE NOT NULL, PASSWORD VARCHAR(255), is_Active BIT)");
    connection.createStatement().execute(
            "INSERT INTO TEST VALUES(1, 'user1', 'password1', 1)");
    connection.createStatement().execute(
            "INSERT INTO TEST VALUES(2, 'user2', 'password2', 0)");
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