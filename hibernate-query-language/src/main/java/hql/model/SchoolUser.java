package hql.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
public class SchoolUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "address", nullable = false, length = 100)
  private String physicalAddress;

  @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
  @Column(name = "phone", nullable = false, length = 20)
  private String phone;

  @Email
  @Column(name = "email", unique = true, nullable = false, length = 100)
  private String email;

  @Column(name = "population", nullable = false)
  private int population;
}
