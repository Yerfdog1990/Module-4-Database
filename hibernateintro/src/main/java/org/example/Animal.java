package org.example;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Animal {
  @Id
  @Column(name = "id", nullable = false)
  int id;

  @Column(name = "name", nullable = false)
  String name;

  @Column(name = "family", nullable = false)
  String family;

  @Column(name = "childName", nullable = false)
  String childName;

  // constructor
  public Animal(int id, String name, String family, String childName) {
    this.id = id;
    this.name = name;
    this.family = family;
    this.childName = childName;
  }
}
