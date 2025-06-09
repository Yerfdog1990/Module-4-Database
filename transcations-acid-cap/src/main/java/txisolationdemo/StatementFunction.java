package txisolationdemo;

import java.sql.SQLException;
import java.sql.Statement;

public interface StatementFunction<T> {
  T accept(Statement statement) throws SQLException, InterruptedException;
}
