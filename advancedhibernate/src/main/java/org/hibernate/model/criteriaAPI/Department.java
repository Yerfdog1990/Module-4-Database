package org.hibernate.model.criteriaAPI;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "department")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true) // Use explicit fields for toString
public class Department {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ToString.Include private String name;

  @OneToMany(
      mappedBy = "department",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  private List<Employee> employees = new java.util.ArrayList<>();

  public Department(String name) {
    this.name = name;
  }

  // Prevent recursive toString() - we exclude employees from the string representation
  @Override
  public String toString() {
    return "Department{id=" + id + ", name='" + name + "'}";
  }
}
