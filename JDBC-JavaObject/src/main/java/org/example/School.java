package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class School {
  private int id;
  private String name;
  private String city;
}
