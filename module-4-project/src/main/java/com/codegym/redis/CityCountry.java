package com.codegym.redis;

import com.codegym.domain.Continent;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CityCountry {
  private Integer id;

  private String name;

  private String district;

  private Integer population;

  private String countryCode;

  private String alternativeCountryCode;

  private String countryName;

  private Continent continent;

  private String countryRegion;

  private BigDecimal countrySurfaceArea;

  private Integer countryPopulation;

  private Set<Language> languages;

  // Getters and Setters to be implemented using lombok dependency
}
