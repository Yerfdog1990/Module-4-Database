package entities;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Employee {
  @Id private Integer id;

  private String name;

  public Employee(int id, String name) {
    this.id = id;
    this.name = name;
  }
}
