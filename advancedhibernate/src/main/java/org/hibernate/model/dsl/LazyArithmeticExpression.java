package org.hibernate.model.dsl;

import java.util.function.Function;

public class LazyArithmeticExpression<T> implements ArithmeticOperations<T> {
  private Function<T, T> computation;
  private final T initialOperand;

  // Constructor
  public LazyArithmeticExpression(T initialOperand) {
    this.initialOperand = initialOperand;
  }

  @Override
  public ArithmeticOperations<T> applyOperator(BinaryOperator<T> operator, T operand) {
    Function<T, T> currentStep = t -> operator.apply(t, operand);
    if (this.computation == null) {
      this.computation = currentStep;
    } else {
      this.computation = this.computation.andThen(currentStep);
    }
    return this;
  }

  @Override
  public T getResult() {
    return computation.apply(initialOperand);
  }
}
