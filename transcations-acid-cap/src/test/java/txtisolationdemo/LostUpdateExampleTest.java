package txtisolationdemo;

import static txisolationdemo.JdbcUtils.doWithStatement;
import static txisolationdemo.JdbcUtils.setUpDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class LostUpdateExampleTest {

  // Update account balance using a read-modify-write cycle
  private void updateBalance(int accountId, int amount) throws SQLException, InterruptedException {
    doWithStatement(
        statement -> {
          try {
            // Step 1: Read the current balance
            String selectBalance = "SELECT balance FROM accounts WHERE id = " + accountId;
            ResultSet resultSet = statement.executeQuery(selectBalance);

            if (resultSet.next()) {
              int balance = resultSet.getInt("balance");
              System.out.printf(
                  "[%s] - Current balance: %d%n", Thread.currentThread().getName(), balance);

              // Simulate race condition
              Thread.sleep(Math.round(Math.random() * 1000));

              // Step 2: Update balance
              int updatedBalance = balance + amount;
              String update =
                  "UPDATE accounts SET balance = " + updatedBalance + " WHERE id = " + accountId;
              statement.executeUpdate(update);
              System.out.printf(
                  "[%s] - Updated balance: %d%n", Thread.currentThread().getName(), updatedBalance);
            }
          } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
          }
        });
  }

  // Display current account balance
  private void displayBalance(int accountId) throws SQLException, InterruptedException {
    doWithStatement(
        statement -> {
          try {
            String query = "SELECT balance FROM accounts WHERE id = " + accountId;
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
              int balance = resultSet.getInt("balance");
              System.out.printf(
                  "[%s] - Account %d balance: %d%n",
                  Thread.currentThread().getName(), accountId, balance);
            }
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
  }

  // Test case that simulates a lost update scenario
  @Test
  void recreateLostUpdate() throws SQLException, InterruptedException {
    // Create a table and insert a row
    setUpDatabase();

    // Run concurrent balance updates
    Thread t1 =
        new Thread(
            () -> {
              try {
                updateBalance(1, 100);
              } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
              }
            });

    Thread t2 =
        new Thread(
            () -> {
              try {
                updateBalance(1, -150);
              } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
              }
            });

    t1.start();
    t2.start();
    t1.join();
    t2.join();

    // Show final balance
    displayBalance(1); // Expected result may vary due to race condition
  }
}
