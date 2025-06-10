package txtisolationdemo;

import static txisolationdemo.JdbcUtils.doWithStatement;
import static txisolationdemo.JdbcUtils.setUpDatabase;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import txisolationdemo.JdbcUtils;

public class LostUpdateExampleTest {

  // Update account balance using a read-modify-write cycle
  private void updateBalance(int accountId, int amount) throws SQLException, InterruptedException {
    doWithStatement(
        statement -> {
          // Step 1: Read the current balance
          int balance = JdbcUtils.queryBalance(statement);
          System.out.printf(
              "[%s] - Current balance: %d%n", Thread.currentThread().getName(), balance);

          // Simulate race condition
          try {
            Thread.sleep(Math.round(Math.random() * 1000));
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }

          // Step 2: Update balance
          int updatedBalance = balance + amount;
          JdbcUtils.updateBalance(updatedBalance, statement);
          System.out.printf(
              "[%s] - Updated balance: %d%n", Thread.currentThread().getName(), updatedBalance);
        });
  }

  // Display current account balance
  private void displayBalance() throws SQLException, InterruptedException {
    JdbcUtils.queryBalance();
    System.out.printf(
        "[%s] - Account balance: %d%n", Thread.currentThread().getName(), JdbcUtils.queryBalance());
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
    displayBalance(); // Expected result may vary due to race condition
  }
}
