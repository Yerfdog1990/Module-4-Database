package org.hibernate.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "citizenship")
@Data
@NoArgsConstructor
public class Citizenship {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String country;

  // Constructor
  public Citizenship(String country) {
    this.country = country;
  }
}
