package ormdemo;

import java.sql.SQLException;

public class UserServiceImpl implements IUserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public boolean authenticate(String username, String password) throws SQLException {
    String storedPassword = null;
    try {
      User user = userRepository.getUser(username);
      if (user == null) return false;
      // Encode the input password and compare with the stored password
      String encodedPassword = passwordEncoder.encode(password);
      return encodedPassword.equals(user.getPassword()) && user.isActive();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
