package hql.hql.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserHQL {
  @Id private int id;

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "is_active", nullable = false)
  private boolean isActive;

  public UserHQL(int id, String username, String password, boolean isActive) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.isActive = isActive;
  }
}
