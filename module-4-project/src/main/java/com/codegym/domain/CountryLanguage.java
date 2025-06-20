package com.codegym.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Table(schema = "world", name = "country_language")
@Data
@NoArgsConstructor
public class CountryLanguage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "country_id")
  private Country country;

  private String language;

  @Column(name = "is_official", columnDefinition = "BIT")
  @Type(type = "org.hibernate.type.NumericBooleanType")
  private Boolean isOfficial;

  private BigDecimal percentage;

  // Getters and Setters to be implemented using lombok dependency
}
