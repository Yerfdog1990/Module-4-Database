package priorknowledge;

import java.util.function.Function;

public class MathFunctions {
  // function f(x)
  private static double fOfx(double input) {
    Function<Double, Double> f = x -> x * 5;
    System.out.printf("f(%.1f) = %.1f\n", input, f.apply(input));
    return f.apply(input);
  }

  // function f(g)
  private static double gOfx(double input) {
    Function<Double, Double> g = x -> x + 10;
    System.out.printf("g(%.1f) = %.1f\n", input, g.apply(input));
    return g.apply(input);
  }

  // composite function fOg(x)
  private static double fOg(double input) {
    Function<Double, Double> f = x -> x * 5;
    Function<Double, Double> g = x -> x + 10;
    System.out.printf("fog(%.1f) = %.1f\n", input, f.compose(g).apply(input));
    return f.compose(g).apply(input);
  }

  // composite function gOf(x)
  private static double gOf(double input) {
    Function<Double, Double> g = x -> x + 10;
    Function<Double, Double> f = x -> x * 5;
    System.out.printf("gof(%.1f) = %.1f\n", input, g.compose(f).apply(input));
    return g.compose(f).apply(input);
  }

  // f(x) andThen g(x)
  private static double fOfxThengOfx(double input) {
    Function<Double, Double> f = x -> x * 5;
    Function<Double, Double> g = x -> x + 10;
    System.out.printf("f(%.1f) andThen g(%.1f) = %.1f\n", input, input, f.andThen(g).apply(input));
    return f.andThen(g).apply(input);
  }

  // g(x) andThen f(x)
  private static double gOfxThengOfx(double input) {
    Function<Double, Double> f = x -> x * 5;
    Function<Double, Double> g = x -> x + 10;
    System.out.printf("g(%.1f) andThen f(%.1f) = %.1f\n", input, input, g.andThen(f).apply(input));
    return g.andThen(f).apply(input);
  }

  // identity
  private static double identity(double input) {
    Function<Double, Double> identity = x -> x;
    System.out.printf("identity(%.1f) = %.1f\n", input, identity.apply(input));
    return identity.apply(input);
  }

  public static void main(String[] args) {
    fOfx(10);
    gOfx(10);
    fOg(10);
    gOf(10);
    fOfxThengOfx(10);
    gOfxThengOfx(10);
    identity(10);
  }
}
