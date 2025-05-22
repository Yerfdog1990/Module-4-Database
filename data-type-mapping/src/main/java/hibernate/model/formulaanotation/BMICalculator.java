package hibernate.model.formulaanotation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "bmi_calculator")
public class BMICalculator {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Long id;

  @Column(name = "weight", nullable = false)
  private double weight;

  @Column(name = "height", nullable = false)
  private double height;

  @Formula("weight/(height * height)")
  private double BMI;

  // No args constructor
  public BMICalculator(double weight, double height) {
    if (weight <= 0 || height <= 0) {
      throw new IllegalArgumentException("Weight and height must be greater than zero");
    }
    this.weight = weight;
    this.height = height;
  }
}
