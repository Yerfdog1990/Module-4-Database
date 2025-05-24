package mappings.model.typeannotation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Entity
@Table(name = "employee")
@NoArgsConstructor
@Getter
@Setter
public class Employee {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;

  @Column(name = "numeric_code")
  @JdbcTypeCode(Types.INTEGER)
  private String numericCode;

  @Column(name = "is_correct")
  @JdbcTypeCode(Types.INTEGER)
  private Boolean isCorrect;

  // No args constructor
  public Employee(String name, String numericCode) {
    this.name = name;
    this.numericCode = numericCode; // Parse the string to Integer
  }
}
