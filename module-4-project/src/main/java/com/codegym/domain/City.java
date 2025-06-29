package com.codegym.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "world", name = "city")
@EqualsAndHashCode(of = {"id"})
@Data
@NoArgsConstructor
public class City {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  @ManyToOne
  @JoinColumn(name = "country_id")
  private Country country;

  private String district;

  private Integer population;

  // Getters and Setters to be implemented using lombok dependency

}
