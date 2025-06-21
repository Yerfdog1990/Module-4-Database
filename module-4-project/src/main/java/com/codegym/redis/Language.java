package com.codegym.redis;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Language {
  private String language;
  private Boolean isOfficial;
  private BigDecimal percentage;

  // Getters and Setters to be implemented using lombok dependency
}
