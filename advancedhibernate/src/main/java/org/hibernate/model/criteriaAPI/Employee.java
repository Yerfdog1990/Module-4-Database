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
@ToString(onlyExplicitlyIncluded = true) // Use explicit fields for toString
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ToString.Include private String name;

  private String occupation;

  private Double salary;

  @ManyToOne private Department department;

  public Employee(String name, String occupation, Double salary, Department department) {
    this.name = name;
    this.occupation = occupation;
    this.salary = salary;
    this.department = department;
  }

  // Prevent recursive toString() - exclude department from the string representation
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
