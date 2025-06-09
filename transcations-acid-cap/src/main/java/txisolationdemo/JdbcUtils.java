package txisolationdemo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

public class JdbcUtils {
  private static final String JdbcUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
  private static final String username = "sa";
  private static final String password = "";

  public static void doWithStatement(Consumer<Statement> consumer)
      throws SQLException, InterruptedException {
    try (Connection connection = DriverManager.getConnection(JdbcUrl, username, password)) {
      connection.setAutoCommit(false);
      try (Statement statement = connection.createStatement()) {
        consumer.accept(statement);
        connection.commit();
      } catch (SQLException e) {
        connection.rollback();
        throw e;
      } finally {
        connection.setAutoCommit(true);
      }
    }
  }

  public static void setUpDatabase() throws SQLException, InterruptedException {
    JdbcUtils.doWithStatement(
        statement -> {
          try {
            statement.execute("CREATE TABLE accounts (id INT PRIMARY KEY, balance INT)");
            statement.execute("INSERT INTO accounts (id, balance) VALUES (1, 100)");
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
  }
}
