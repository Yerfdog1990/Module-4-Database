package org.hibernate.model.criteriaAPI;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ToString.Include private String name;

  private String occupation;

  private Double salary;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id", nullable = false) // Enforce valid department reference
  private Department department;

  public Employee(String name, String occupation, Double salary, Department department) {
    this.name = name;
    this.occupation = occupation;
    this.salary = salary;
    this.department = department;
  }

  // Helper method for unlinking from department
  public void unlinkDepartment() {
    if (this.department != null) {
      this.department.getEmployees().remove(this);
      this.department = null;
    }
  }

  @Override
  public String toString() {
    return "Employee{id="
        + id
        + ", name='"
        + name
        + "', occupation='"
        + occupation
        + "', salary="
        + salary
        + "}";
  }
}
