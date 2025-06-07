package org.hibernate.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profession")
@Data
@NoArgsConstructor
public class Profession {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  // Constructor
  public Profession(String name) {
    this.name = name;
  }
}
