package hibernate.model.criteria.dsl;

// Using the builder pattern to wrap a computation expressed in terms of an AST
public class EagerArithmeticExpression<T> implements ArithmeticOperations<T> {
  private T currentResultValue;

  // Constructor
  public EagerArithmeticExpression(T currentResultValue) {
    this.currentResultValue = currentResultValue;
  }

  @Override
  public ArithmeticOperations<T> applyOperator(BinaryOperator<T> operator, T operand) {
    this.currentResultValue = operator.apply(this.currentResultValue, operand);
    return this;
  }

  @Override
  public T getResult() {
    return this.currentResultValue;
  }
}
