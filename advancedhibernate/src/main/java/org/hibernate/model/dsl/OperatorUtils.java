package org.hibernate.model.dsl;

public class OperatorUtils {
  static <T> T apply(BinaryOperator<T> operator, T t1, T t2) {
    if (t1 == null || t2 == null) {
      throw new IllegalArgumentException("Invalid input (null not allowed)");
    }
    return operator.apply(t1, t2);
  }
}
