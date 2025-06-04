package hibernate.model.criteria.dsl;

public class IntegerSum implements BinaryOperator<Integer> {
  @Override
  public Integer apply(Integer t1, Integer t2) {
    if (t1 == null || t2 == null) {
      throw new IllegalArgumentException("Invalid input (null not allowed)");
    }
    System.out.println("Applying integer sum operator");
    return OperatorUtils.apply(Integer::sum, t1, t2);
  }
}
