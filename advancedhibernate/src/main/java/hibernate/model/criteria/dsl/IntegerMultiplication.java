package hibernate.model.criteria.dsl;

public class IntegerMultiplication implements BinaryOperator<Integer> {
  private Integer multiplication(Integer t1, Integer t2) {
    return t1 * t2;
  }

  @Override
  public Integer apply(Integer t1, Integer t2) {
    if (t1 == null || t2 == null) {
      throw new IllegalArgumentException("Invalid input (null not allowed)");
    }
    System.out.println("Applying integer multiplication operator");
    return OperatorUtils.apply(this::multiplication, t1, t2);
  }
}
