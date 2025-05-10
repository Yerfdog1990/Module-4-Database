package ormdemo;

public class NoopPasswordEncoder implements PasswordEncoder {
  @Override
  public String encode(String password) {
    return password;
  }
}
