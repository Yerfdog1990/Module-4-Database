package org.example;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Employee {
  private int id;
  private String name;
  private String position;
  private int salary;

  // Constructor
  public Employee(int id, String name, String position, int salary) {
    this.id = id;
    this.name = name;
    this.position = position;
    this.salary = salary;
  }
}
