package txisolationdemo;

import java.sql.Statement;
import java.util.function.Function;

public interface StatementFunction<T> extends Function<Statement, T> {}
