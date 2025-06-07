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

  @ManyToOne
  @JoinColumn(name = "city_id", nullable = false)
  private City city;

  public Person(String name, Integer age, City city) {
    this.name = name;
    this.age = age;
    this.city = city;
  }
}
