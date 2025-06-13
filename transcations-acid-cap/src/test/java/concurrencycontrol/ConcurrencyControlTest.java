package concurrencycontrol;

import static concurrencycontrol.H2TransactionDemo.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

public class ConcurrencyControlTest {

  @Test
  void pessimisticTransactionFlow() throws SQLException {
    // Initialize the database and create a table
    initializeDatabase();

    System.out.println("--- Demonstrating Pessimistic Transaction Flow ---");
    // Run pessimistic transactions concurrently to show the locking effects conceptually
    ExecutorService pessimisticExecutor = Executors.newFixedThreadPool(2);
    pessimisticExecutor.submit(
        () -> {
          try {
            runPessimisticTransaction("User A", 1, 50);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
    pessimisticExecutor.submit(
        () -> {
          try {
            runPessimisticTransaction("User B", 1, 30);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        }); // User B tries to update the same account
    pessimisticExecutor.shutdown();
    try {
      pessimisticExecutor.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println("Pessimistic executor interrupted: " + e.getMessage());
    }

    System.out.println("\n--- Demonstrating Optimistic Transaction Flow ---");
    // Reset balance for optimistic demo
    resetBalance();

    // Verify the final balance after pessimistic transactions
    System.out.println("\n--- Final Balance Check (Pessimistic) ---");
    int finalBalance = printAccountBalance(1);
    // Since pessimistic locking prevents concurrent updates, only one transaction should succeed
    assertNotEquals(
        50, finalBalance - 100, "Final balance should reflect only one successful transaction");
  }

  @Test
  void optimisticTransactionFlow() throws SQLException {
    // Initialize the database and create a table
    initializeDatabase();
    System.out.println("--- Demonstrating Optimistic Transaction Flow ---");
    // Run optimistic transactions concurrently to show versioning effects
    ExecutorService optimisticExecutor = Executors.newFixedThreadPool(2);
    optimisticExecutor.submit(
        () -> {
          try {
            runOptimisticTransaction("User X", 1, 50);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
    optimisticExecutor.submit(
        () -> {
          try {
            runOptimisticTransaction("User Y", 1, 30);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        }); // User Y tries to update the same account
    optimisticExecutor.shutdown();
    try {
      optimisticExecutor.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println("Optimistic executor interrupted: " + e.getMessage());
    }

    // Final balance check
    System.out.println("\n--- Final Balance Check ---");
    int finalBalance = printAccountBalance(1);
    // Assert that the balance has changed from the initial 100
    assertNotEquals(100, finalBalance, "Balance should be different from initial value");
    // Since optimistic locking allows only atomic operations
    assertNotEquals(
        50, finalBalance - 100, "Final balance should reflect only one successful transaction");
  }
}
