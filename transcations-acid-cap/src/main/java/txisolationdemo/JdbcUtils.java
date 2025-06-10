package txisolationdemo;

import java.sql.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class JdbcUtils {
  // JDBC URL for a shared in-memory H2 database
  private static final String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
  private static final String USER = "sa";
  private static final String PASSWORD = "";

  private static final Integer ACCOUNT_ID = 1;

  private static final Integer ISOLATION_LEVEL = Connection.TRANSACTION_READ_UNCOMMITTED;

  // Executes a consumer operation on a SQL statement without returning a result
  public static void doWithStatement(Consumer<Statement> consumer) {
    StatementFunction<Void> fx =
        statement -> {
          consumer.accept(statement);
          return null;
        };

    doWithStatement(fx);
  }

  // Creates a new database connection with a specified transaction isolation level
  public static Connection createConnection(int isolationLevel) {
    try {
      Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(isolationLevel);
      return connection;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  // Queries the account balance using the provided database connection
  public static Integer queryBalanceInConnection(Connection connection) {
    try {
      Statement statement = connection.createStatement();
      return queryBalance(statement);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  // Updates the account balance using the provided connection and amount
  public static void updateBalanceInConnection(int amount, Connection connection) {
    try {
      Statement statement = connection.createStatement();
      updateBalance(amount, statement);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  // Executes a function on a SQL statement and returns its result
  public static <T> T doWithStatement(Function<Statement, T> fx) {
    try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
      connection.setAutoCommit(false); // Disable autocommit
      connection.setTransactionIsolation(ISOLATION_LEVEL);
      try (Statement statement = connection.createStatement()) {
        T result = fx.apply(statement);
        connection.commit();
        return result;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  // Retrieves the current balance from the account table
  public static int queryBalance() {
    StatementFunction<Integer> fx = JdbcUtils::queryBalance;
    return doWithStatement(fx);
  }

  // Queries the account balance using the provided SQL statement
  public static Integer queryBalance(Statement statement) {
    String queryBalance = "SELECT balance FROM accounts WHERE id = " + ACCOUNT_ID;
    try {
      ResultSet queryBalanceResultSet = statement.executeQuery(queryBalance);
      if (queryBalanceResultSet.next()) {
        return queryBalanceResultSet.getInt(1);
      } else return null;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  // Updates the account balance by the specified amount using the provided statement
  public static void updateBalance(int amount, Statement statement) {
    String update =
        "UPDATE accounts SET balance = balance + " + amount + " WHERE id = " + ACCOUNT_ID;
    try {
      statement.execute(update);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  // Initializes the database by creating the account table and inserting initial data
  public static void setUpDatabase() {
    JdbcUtils.doWithStatement(
        statement -> {
          String createTable = "CREATE TABLE accounts (id INT PRIMARY KEY, balance INT)";
          try {
            statement.execute(createTable);
            String insert = "INSERT INTO accounts (id, balance) VALUES (1, 100)";
            statement.execute(insert);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
  }
}
