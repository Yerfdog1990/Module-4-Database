package concurrencycontrol;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

public class H2TransactionDemo {

  /** Initializes the H2 in-memory database and creates an ACCOUNTS table. */
  private static Connection doWithConnection(Consumer<Connection> connection) throws SQLException {
    String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    String USER = "sa";
    String PASSWORD = "";
    Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    connection.accept(conn);
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        System.err.println("Error closing connection: " + e.getMessage());
      }
    }
    return conn;
  }

  static void initializeDatabase() throws SQLException {
    doWithConnection(
        connection -> {
          try (Statement statement = connection.createStatement()) {
            // Drop the table if it exists to ensure a clean start
            statement.execute("DROP TABLE IF EXISTS ACCOUNTS");
            // Create ACCOUNTS table with 'id', 'balance', and 'version' (for optimistic locking)
            statement.execute(
                "CREATE TABLE ACCOUNTS ("
                    + "id INTEGER PRIMARY KEY, "
                    + "balance DOUBLE, "
                    + "version INTEGER"
                    + // Add a version for optimistic locking
                    ")");
            // Insert initial account data
            statement.execute("INSERT INTO ACCOUNTS (id, balance, version) VALUES (1, 100.00, 0)");
            System.out.println(
                "Database initialized and ACCOUNTS table created with initial balance: 100.00");
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
  }

  /** Resets the account balance and version for a fresh optimistic demo run. */
  static void resetBalance() throws SQLException {
    doWithConnection(
        connection -> {
          try {
            connection.setAutoCommit(false);
            String updateSql = "UPDATE ACCOUNTS SET balance = ?, version = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
              pstmt.setDouble(1, 100.00);
              pstmt.setInt(2, 0);
              pstmt.setInt(3, 1);
              pstmt.executeUpdate();
              connection.commit();
              System.out.println("\nAccount balance reset to 100 for optimistic demo.");
            } catch (SQLException e) {
              connection.rollback();
              System.err.println("Failed to reset balance: " + e.getMessage());
            }
          } catch (SQLException ex) {
            throw new RuntimeException(ex);
          }
        });
  }

  /**
   * Demonstrates a pessimistic transaction flow using SERIALIZABLE isolation level. In H2, explicit
   * row-level locking (like SQL Server's WITH (ROWLOCK, XLOCK) or PostgreSQL's SELECT FOR UPDATE)
   * is not directly available for standard transactional locks. However, the SERIALIZABLE isolation
   * level provides strong guarantees by preventing phenomena like dirty reads, non-repeatable
   * reads, and phantom reads, effectively simulating a pessimistic control by making concurrent
   * transactions wait or fail.
   *
   * <p>Transaction Phases (Conceptual): 1. Validate (Acquire Locks): Set isolation level to
   * SERIALIZABLE. The database system takes care of acquiring the necessary shared/exclusive locks
   * (or other concurrency control mechanisms like predicate locks) to ensure that the data read
   * remains stable for the duration of the transaction. If another transaction has an incompatible
   * lock, this transaction will wait. 2. Read: Retrieve the account balance. 3. Compute: Calculate
   * the new balance. 4. Write (Release Locks): Update the balance. Locks are held until
   * commit/rollback.
   */
  static void runPessimisticTransaction(String userName, int accountId, double amountToDeposit)
      throws SQLException {
    doWithConnection(
        connection -> {
          try {
            connection.setAutoCommit(false); // Start transaction

            // Step 1: Validate (Acquire Locks) - Set isolation level to SERIALIZABLE
            // This is crucial for pessimistic concurrency control. It ensures that
            // no other concurrent transaction can modify the data being read or
            // inserted/updated that would affect the outcome of this transaction.
            // If another transaction holds an incompatible lock, this transaction
            // might wait until that lock is released.
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            System.out.println(userName + ": Set transaction isolation to SERIALIZABLE.");

            // Introduce a small delay to simulate concurrent access and potential waiting
            Thread.sleep(500);

            // Step 2: Read - Retrieve the current balance
            String selectSql = "SELECT balance FROM ACCOUNTS WHERE id = ?";
            double currentBalance;
            try (PreparedStatement pstmt = connection.prepareStatement(selectSql)) {
              pstmt.setInt(1, accountId);
              ResultSet rs = pstmt.executeQuery();
              if (rs.next()) {
                currentBalance = rs.getDouble("balance");
                System.out.println(
                    userName + ": Current balance read (pessimistic): " + currentBalance);
              } else {
                throw new SQLException("Account not found: " + accountId);
              }
            }

            // Step 3: Compute - Calculate the new balance
            double newBalance = currentBalance + amountToDeposit;
            System.out.println(userName + ": New balance computed: " + newBalance);

            // Simulate some complex computation or external call that takes time
            Thread.sleep(1000);

            // Step 4: Write - Update the balance
            String updateSql = "UPDATE ACCOUNTS SET balance = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
              pstmt.setDouble(1, newBalance);
              pstmt.setInt(2, accountId);
              int rowsAffected = pstmt.executeUpdate();
              if (rowsAffected > 0) {
                System.out.println(userName + ": Balance updated successfully (pessimistic).");
                connection.commit(); // Commit the transaction, releasing locks
                System.out.println(
                    userName
                        + ": Transaction committed (pessimistic). Final balance: "
                        + newBalance);
              } else {
                // This scenario is less likely with SERIALIZABLE but good practice
                throw new SQLException("Failed to update balance for account " + accountId);
              }
            }

          } catch (SQLException | InterruptedException e) {
            System.err.println(userName + ": Pessimistic transaction failed: " + e.getMessage());
            if (connection != null) {
              try {
                connection.rollback(); // Rollback on error
                System.err.println(userName + ": Transaction rolled back (pessimistic).");
              } catch (SQLException ex) {
                System.err.println(userName + ": Error during rollback: " + ex.getMessage());
              }
            }
          } finally {
            if (connection != null) {
              try {
                connection.close();
              } catch (SQLException e) {
                System.err.println(userName + ": Error closing connection: " + e.getMessage());
              }
            }
          }
        });
  }

  /**
   * Demonstrates an optimistic transaction flow using a versioning approach. In this approach,
   * transactions do not acquire locks upfront. Instead, they read data, perform computations, and
   * then "validate" just before writing by checking if the data has been modified by another
   * transaction. If a conflict is detected, the transaction aborts and typically retries.
   *
   * <p>Transaction Phases: 1. Read: Retrieve the account balance AND its current version. 2.
   * Compute: Calculate the new balance. 3. Validate: Before writing, check if the `version` column
   * for the row is still the same as what was initially read. If not, another transaction modified
   * it. This is done implicitly in the UPDATE statement's WHERE clause. 4. Write: Update the
   * balance and increment the version number. If the UPDATE statement affects 0 rows, it means
   * validation failed (the version didn't match), and the transaction should be rolled back and
   * retried.
   */
  static void runOptimisticTransaction(String userName, int accountId, double amountToDeposit)
      throws SQLException {
    doWithConnection(
        connection -> {
          int maxRetries = 3;
          for (int retry = 0; retry < maxRetries; retry++) {
            try {
              connection.setAutoCommit(false); // Start transaction
              connection.setTransactionIsolation(
                  Connection.TRANSACTION_READ_COMMITTED); // Lower isolation for optimistic

              // Step 1: Read - Retrieve the current balance and version
              String selectSql = "SELECT balance, version FROM ACCOUNTS WHERE id = ?";
              double currentBalance;
              int currentVersion;
              try (PreparedStatement pstmt = connection.prepareStatement(selectSql)) {
                pstmt.setInt(1, accountId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                  currentBalance = rs.getDouble("balance");
                  currentVersion = rs.getInt("version");
                  System.out.println(
                      userName
                          + ": Current balance read (optimistic, retry "
                          + (retry + 1)
                          + "): "
                          + currentBalance
                          + ", Version: "
                          + currentVersion);
                } else {
                  throw new SQLException("Account not found: " + accountId);
                }
              }

              // Simulate some work being done (during which a conflict could occur)
              Thread.sleep(800);

              // Step 2: Compute - Calculate the new balance
              double newBalance = currentBalance + amountToDeposit;
              int nextVersion = currentVersion + 1;
              System.out.println(
                  userName
                      + ": New balance computed: "
                      + newBalance
                      + ", Next Version: "
                      + nextVersion);

              // Step 3 & 4: Validate and Write - Update the balance, increment version, and
              // validate
              // The WHERE clause 'AND version = ?' acts as the validation.
              // If another transaction has already updated the row and incremented the version,
              // this UPDATE statement will affect 0 rows, signaling a conflict.
              String updateSql =
                  "UPDATE ACCOUNTS SET balance = ?, version = ? WHERE id = ? AND version = ?";
              try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                pstmt.setDouble(1, newBalance);
                pstmt.setInt(2, nextVersion);
                pstmt.setInt(3, accountId);
                pstmt.setInt(4, currentVersion); // Validation check
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                  connection.commit(); // Commit the transaction
                  System.out.println(
                      userName
                          + ": Balance updated successfully (optimistic). New balance: "
                          + newBalance
                          + ", New Version: "
                          + nextVersion);
                  return; // Transaction successful, exit retry loop
                } else {
                  // Validation failed: another transaction modified the data.
                  connection.rollback(); // Roll back this transaction
                  System.out.println(
                      userName
                          + ": Optimistic update conflict detected for account "
                          + accountId
                          + ". Retrying...");
                  // Continue to the next retry attempt
                }
              }

            } catch (SQLException | InterruptedException e) {
              System.err.println(
                  userName
                      + ": Optimistic transaction failed (retry "
                      + (retry + 1)
                      + "): "
                      + e.getMessage());
              if (connection != null) {
                try {
                  connection.rollback();
                  System.err.println(userName + ": Transaction rolled back (optimistic).");
                } catch (SQLException ex) {
                  System.err.println(userName + ": Error during rollback: " + ex.getMessage());
                }
              }
            } finally {
              if (connection != null) {
                try {
                  connection.close();
                } catch (SQLException e) {
                  System.err.println(userName + ": Error closing connection: " + e.getMessage());
                }
              }
            }
          }
          System.out.println(
              userName
                  + ": Failed to complete optimistic transaction after "
                  + maxRetries
                  + " retries.");
        });
  }

  /**
   * Prints the current balance of a given account.
   *
   * @return
   */
  static int printAccountBalance(int accountId) throws SQLException {
    doWithConnection(
        connection -> {
          try {
            PreparedStatement pstmt =
                connection.prepareStatement("SELECT balance, version FROM ACCOUNTS WHERE id = ?");
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
              System.out.println(
                  "Account "
                      + accountId
                      + " final balance: "
                      + rs.getDouble("balance")
                      + ", Version: "
                      + rs.getInt("version"));
            } else {
              System.out.println("Account " + accountId + " not found.");
            }
          } catch (SQLException e) {
            System.err.println("Error printing account balance: " + e.getMessage());
          }
        });
    return accountId;
  }
}
