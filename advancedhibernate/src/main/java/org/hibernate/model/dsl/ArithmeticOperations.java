package org.hibernate.model.dsl;

public interface ArithmeticOperations<T> {
  ArithmeticOperations<T> applyOperator(BinaryOperator<T> operator, T operand);

  T getResult();
}
