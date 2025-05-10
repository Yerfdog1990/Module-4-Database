package ormdemo;

import lombok.Data;
// POJO -> Plain Old Java Object

@Data
public class User {
  private String username;
  private String password;
  private boolean isActive;
}
