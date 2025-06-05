package org.hibernate.model.criteriaAPI;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
public class Person {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private Integer age;

  @ManyToOne private City city;

  public Person(String name, Integer age) {
    this.name = name;
    this.age = age;
  }
}
