package org.hibernate.model.criteriaAPI;

import jakarta.persistence.*;
import java.util.ArrayList;
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
@ToString(onlyExplicitlyIncluded = true)
public class Department {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ToString.Include private String name;

  // Cascade only persist and merge to avoid accidental deletes
  @OneToMany(
      mappedBy = "department",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE},
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  private List<Employee> employees = new ArrayList<>();

  public Department(String name) {
    this.name = name;
  }

  public void addEmployee(Employee employee) {
    employees.add(employee);
    employee.setDepartment(this);
  }

  public void removeEmployee(Employee employee) {
    employees.remove(employee);
    employee.setDepartment(null);
  }

  @Override
  public String toString() {
    return "Department{id=" + id + ", name='" + name + "'}";
  }
}
