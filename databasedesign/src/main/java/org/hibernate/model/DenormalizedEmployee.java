package org.hibernate.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "denormalized_employee")
@Data
@NoArgsConstructor
public class DenormalizedEmployee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String email;

  private String department;
  private String profession;
  private String citizenship;

  // Constructor
  public DenormalizedEmployee(
      String name, String email, String department, String profession, String citizenship) {
    this.name = name;
    this.email = email;
    this.department = department;
    this.profession = profession;
    this.citizenship = citizenship;
  }
}
