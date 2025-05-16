package org.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Person {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false)
  private String name;

  @Column(unique = true, nullable = false)
  private String email;

  @Column private String phone;

  @Column private String address;

  @Column private String citizenship;

  // Constructor for JPA
  public Person(String name, String email, String phone, String address, String citizenship) {
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.address = address;
    this.citizenship = citizenship;
  }
}
