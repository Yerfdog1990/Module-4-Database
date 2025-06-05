package dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.model.dsl.*;
import org.junit.jupiter.api.Test;

public class ArithmeticDSLTest {
  private Integer operand1 = 11;
  private Integer operand2 = 3;
  private Integer operand3 = 14;
  private Integer operand4 = 33;

  @Test
  void showcaseIntegerAddition() {
    // We use the DSL to create an Abstract Syntax Tree for a couple of integer additions (a
    // computation)
    BinaryOperator<Integer> operator = new IntegerSum();
    Integer result = operator.apply(operand1, operand2);
    System.out.println(operand1 + " + " + operand2 + " = " + result);
  }

  @Test
  void showcaseIntegerMultiplication() {
    BinaryOperator<Integer> operator = new IntegerMultiplication();
    Integer result = operator.apply(operand1, operand2);
    System.out.println(operand1 + " * " + operand2 + " = " + result);
  }

  @Test
  void showcaseIntegerSubtraction() {
    BinaryOperator<Integer> operator = new IntegerSubtraction();
    Integer result = operator.apply(operand4, operand2);
    System.out.println(operand4 + " - " + operand2 + " = " + result);
  }

  @Test
  void showcaseDSLPart() {
    BinaryOperator<Integer> addition = new IntegerSum();
    BinaryOperator<Integer> multiplication = new IntegerMultiplication();
    BinaryOperator<Integer> subtraction = new IntegerSubtraction();

    ArithmeticOperations<Integer> expression =
        new EagerArithmeticExpression<>(operand1)
            .applyOperator(multiplication, operand2)
            .applyOperator(addition, operand3)
            .applyOperator(subtraction, operand4);

    Integer result = expression.getResult();
    System.out.println(
        operand1 + " * " + operand2 + " + " + operand3 + " - " + operand4 + " = " + result);
    assertEquals(result, 14);
  }
}
