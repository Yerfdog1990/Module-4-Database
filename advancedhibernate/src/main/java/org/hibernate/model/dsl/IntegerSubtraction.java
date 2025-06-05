package org.hibernate.model.dsl;

public class IntegerSubtraction implements BinaryOperator<Integer> {
  private Integer subtraction(Integer t1, Integer t2) {
    return t1 - t2;
  }

  @Override
  public Integer apply(Integer t1, Integer t2) {
    if (t1 == null || t2 == null) {
      throw new IllegalArgumentException("Invalid input (null not allowed)");
    }
    System.out.println("Applying integer subtraction operator");
    return OperatorUtils.apply(this::subtraction, t1, t2);
  }
}
