package model;

import static hibernate.repository.HibernateUtil.doWithSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import hibernate.model.formulaanotation.BMICalculator;
import org.junit.jupiter.api.Test;

public class BMICalculatorTest {
  @Test
  void verifyBMICalculation() {
    BMICalculator bmiCalculator =
        doWithSession(
            session -> {
              BMICalculator bmi = new BMICalculator(80, 1.8);
              return session.merge(bmi);
            });
    doWithSession(
        session -> {
          BMICalculator foundBmi = session.find(BMICalculator.class, bmiCalculator.getId());
          double bmi = foundBmi.getBMI();
          System.out.println("BMI = " + bmi);
          assertNotNull(foundBmi);
          assertEquals(24.69, foundBmi.getBMI(), 0.01);
          return null;
        });
  }
}
