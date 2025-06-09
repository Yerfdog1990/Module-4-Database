package txisolationdemo;

import java.sql.*;
import java.util.function.Consumer;

public class JdbcUtils {
  private static final String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
  private static final String USERNAME = "sa";
  private static final String PASSWORD = "";
  private static final int ACCOUNT_ID = 1;

  public static void doWithStatement(Consumer<Statement> consumer)
      throws SQLException, InterruptedException {
    StatementFunction<Void> function =
        statement -> {
          consumer.accept(statement);
          return null;
        };
    doWithStatement(function);
  }

  public static <T> T doWithStatement(StatementFunction<T> function)
      throws SQLException, InterruptedException {
    try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
      connection.setAutoCommit(false);
      try (Statement statement = connection.createStatement()) {
        T result = function.accept(statement);
        connection.commit();
        return result;
      } catch (SQLException e) {
        connection.rollback();
        throw e;
      } finally {
        connection.setAutoCommit(true);
      }
    }
  }

  public static int queryBalance() throws SQLException, InterruptedException {
    StatementFunction<Integer> function = JdbcUtils::queryBalance;
    return doWithStatement(function);
  }

  public static Integer queryBalance(Statement statement)
      throws SQLException, InterruptedException {
    String query = "SELECT balance FROM accounts WHERE id = " + ACCOUNT_ID;
    ResultSet resultSet = statement.executeQuery(query);
    if (resultSet.next()) {
      return resultSet.getInt(1);
    } else {
      return null;
    }
  }

  public static void updateBalance(int amount, Statement statement) throws SQLException {
    String update =
        "UPDATE accounts SET balance = balance + " + amount + " WHERE id = " + ACCOUNT_ID;
    statement.executeUpdate(update);
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
