package idempotent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class IdempotentOnlyDemo {

  // H2 Database URL for an in-memory database.
  // DB_CLOSE_DELAY=-1 prevents the database from closing when the last connection is closed.
  private static final String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
  private static final String USER = "sa";
  private static final String PASS = "";

  // Counter for successful operations for observation across threads
  private static final AtomicInteger successCounter = new AtomicInteger(0);

  private static ExecutorService executor;

  public static void main(String[] args) {
    Connection mainConn = null; // Use a dedicated connection for table creation and final display
    try {
      // Register the H2 JDBC driver
      Class.forName("org.h2.Driver");

      // Establish a main connection for setup (table creation) and final verification
      mainConn = DriverManager.getConnection(DB_URL, USER, PASS);
      System.out.println("Connected to H2 in-memory database (main thread).");

      // 1. Create the 'PRODUCTS' table if it doesn't exist
      createTable(mainConn);

      // --- Demonstrating Idempotency in Concurrent Transactions ---

      int numberOfThreads = 5;
      ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

      // Scenario 1: Concurrent Idempotent Upserts (Same Product, Same Data)
      // Multiple threads attempt to upsert the same product with identical data.
      // The final state should be a single record with the specified ID and name.
      System.out.println(
          "\n--- Scenario 1: Concurrent Idempotent Upserts (Same Product, Same Data) ---");
      System.out.println(
          "Multiple threads will attempt to upsert product ID 1 with name 'Concurrent Product'.");
      successCounter.set(0); // Reset counter for this scenario
      CountDownLatch latch1 = new CountDownLatch(numberOfThreads);

      for (int i = 0; i < numberOfThreads; i++) {
        final int threadId = i;
        executor.submit(
            () -> {
              Connection threadConn = null;
              try {
                // Each thread gets its own connection
                threadConn = DriverManager.getConnection(DB_URL, USER, PASS);
                // Simulate some work before the main operation to increase concurrency chance
                Thread.sleep((long) (Math.random() * 50));

                // All threads try to upsert the same product ID and name
                idempotentUpsertProduct(threadConn, 1, "Concurrent Product");
                successCounter
                    .incrementAndGet(); // Increment if the operation completes without a SQL
                // exception

              } catch (SQLException e) {
                System.err.println("Thread " + threadId + " SQL Error: " + e.getMessage());
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
                System.err.println("Thread " + threadId + " interrupted: " + e.getMessage());
              } finally {
                if (threadConn != null) {
                  try {
                    threadConn.close();
                  } catch (SQLException e) {
                    System.err.println(
                        "Thread " + threadId + " error closing connection: " + e.getMessage());
                  }
                }
                latch1.countDown(); // Signal that this thread has completed
              }
            });
      }

      // Wait for all threads in Scenario 1 to complete
      latch1.await(10, TimeUnit.SECONDS);
      System.out.println("All concurrent upsert operations (Scenario 1) completed.");
      System.out.println(
          "Total successful upsert attempts: "
              + successCounter.get()
              + " (Expected: "
              + numberOfThreads
              + " attempts resulting in 1 record)");
      displayProduct(mainConn, 1); // Verify final state: should show 'Concurrent Product'

      // Scenario 2: Concurrent Idempotent Upserts (Same Product, Different Data)
      // Multiple threads attempt to upsert the same product with different names.
      // The final state will be one record, its name determined by which thread's update was last
      // committed.
      System.out.println(
          "\n--- Scenario 2: Concurrent Idempotent Upserts (Same Product, Different Data) ---");
      System.out.println(
          "Multiple threads will attempt to upsert product ID 2 with different names.");
      successCounter.set(0); // Reset counter
      CountDownLatch latch2 = new CountDownLatch(numberOfThreads);
      executor = Executors.newFixedThreadPool(numberOfThreads); // New executor for clean slate

      for (int i = 0; i < numberOfThreads; i++) {
        final int threadId = i;
        executor.submit(
            () -> {
              Connection threadConn = null;
              try {
                threadConn = DriverManager.getConnection(DB_URL, USER, PASS);
                Thread.sleep((long) (Math.random() * 50));

                // All threads try to upsert the same product ID, but with different names
                idempotentUpsertProduct(threadConn, 2, "Product-Name-" + threadId);
                successCounter.incrementAndGet();

              } catch (SQLException e) {
                System.err.println("Thread " + threadId + " SQL Error: " + e.getMessage());
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread " + threadId + " interrupted: " + e.getMessage());
              } finally {
                if (threadConn != null) {
                  try {
                    threadConn.close();
                  } catch (SQLException e) {
                    System.err.println(
                        "Thread " + threadId + " error closing connection: " + e.getMessage());
                  }
                }
                latch2.countDown();
              }
            });
      }

      latch2.await(10, TimeUnit.SECONDS);
      System.out.println("All concurrent upsert operations (Scenario 2) completed.");
      System.out.println(
          "Total successful upsert attempts: "
              + successCounter.get()
              + " (Expected: "
              + numberOfThreads
              + " attempts resulting in 1 record)");
      displayProduct(
          mainConn, 2); // Verify the final state (will be one of the names from Product-Name-X)

      // Scenario 3: Concurrent Idempotent Deletions
      // Multiple threads attempt to delete the same product ID.
      // The final state should be that the product is absent, regardless of how many times deletion
      // was attempted.
      System.out.println("\n--- Scenario 3: Concurrent Idempotent Deletions ---");
      System.out.println(
          "Multiple threads will attempt to delete product ID 1 (which might have been upserted again from scenario 1).");
      // First, ensure product 1 is there to be deleted
      idempotentUpsertProduct(mainConn, 1, "Product To Be Deleted");
      System.out.println("Product ID 1 ensured to exist for deletion test.");
      displayProduct(mainConn, 1);

      successCounter.set(0); // Reset counter
      CountDownLatch latch3 = new CountDownLatch(numberOfThreads);
      executor = Executors.newFixedThreadPool(numberOfThreads);

      for (int i = 0; i < numberOfThreads; i++) {
        final int threadId = i;
        executor.submit(
            () -> {
              Connection threadConn = null;
              try {
                threadConn = DriverManager.getConnection(DB_URL, USER, PASS);
                Thread.sleep((long) (Math.random() * 50));

                // All threads try to delete the same product ID
                idempotentDeleteProduct(threadConn, 1);
                successCounter.incrementAndGet();

              } catch (SQLException e) {
                System.err.println("Thread " + threadId + " SQL Error: " + e.getMessage());
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread " + threadId + " interrupted: " + e.getMessage());
              } finally {
                if (threadConn != null) {
                  try {
                    threadConn.close();
                  } catch (SQLException e) {
                    System.err.println(
                        "Thread " + threadId + " error closing connection: " + e.getMessage());
                  }
                }
                latch3.countDown();
              }
            });
      }

      latch3.await(10, TimeUnit.SECONDS);
      System.out.println("All concurrent delete operations (Scenario 3) completed.");
      System.out.println(
          "Total successful delete attempts: "
              + successCounter.get()
              + " (Expected: "
              + numberOfThreads
              + " attempts resulting in product absence)");
      displayProduct(mainConn, 1); // Verify final state: should be "not found"

    } catch (ClassNotFoundException e) {
      System.err.println("H2 JDBC Driver not found: " + e.getMessage());
    } catch (SQLException e) {
      System.err.println("SQL Error (main thread): " + e.getMessage());
      e.printStackTrace();
    } catch (InterruptedException e) {
      System.err.println(
          "Main thread interrupted while waiting for concurrent tasks: " + e.getMessage());
      Thread.currentThread().interrupt();
    } finally {
      // Shut down the executor service and close the main connection
      if (executor != null && !executor.isTerminated()) {
        executor.shutdownNow(); // Attempt to stop all running tasks
        try {
          executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
          System.err.println("Executor did not terminate in time: " + e.getMessage());
        }
      }
      try {
        if (mainConn != null) {
          mainConn.close();
          System.out.println("\nMain connection closed.");
        }
      } catch (SQLException e) {
        System.err.println("Error closing main connection: " + e.getMessage());
      }
    }
  }

  /**
   * Creates the PRODUCTS table if it does not already exist.
   *
   * @param conn The database connection.
   * @throws SQLException if a database access error occurs.
   */
  private static void createTable(Connection conn) throws SQLException {
    String createTableSQL =
        "CREATE TABLE IF NOT EXISTS PRODUCTS ("
            + "ID INT PRIMARY KEY,"
            + "NAME VARCHAR(255) NOT NULL"
            + ");";
    try (Statement stmt = conn.createStatement()) {
      stmt.execute(createTableSQL);
      System.out.println("Table 'PRODUCTS' created or already exists.");
    }
  }

  /**
   * This method performs an UPSERT (Update or Insert) operation, making it an idempotent operation.
   * Using H2's MERGE INTO statement for concise UPSERT.
   *
   * @param conn The database connection unique to the calling thread.
   * @param id The ID of the product.
   * @param name The name of the product.
   * @throws SQLException if a database access error occurs.
   */
  private static void idempotentUpsertProduct(Connection conn, int id, String name)
      throws SQLException {
    // MERGE INTO attempts to update a row if a matching primary key (ID) exists,
    // otherwise, it inserts a new row. This operation is atomic and ensures consistency
    // even when multiple threads attempt it concurrently for the same ID.
    String mergeSql = "MERGE INTO PRODUCTS KEY(ID) VALUES (?, ?);";
    try (PreparedStatement pstmt = conn.prepareStatement(mergeSql)) {
      pstmt.setInt(1, id);
      pstmt.setString(2, name);
      pstmt.executeUpdate();
      // The number of rows affected might be 0, 1, or 2 depending on the specific DB system
      // and whether it was an insert or update. H2's MERGE typically returns 1 for both.
      // The key is that the final database state is consistent and not duplicated.
    }
  }

  /**
   * Deletes a product by ID. This is also an idempotent operation. Deleting an already deleted item
   * has the same effect as deleting it once.
   *
   * @param conn The database connection unique to the calling thread.
   * @param id The ID of the product to delete.
   * @throws SQLException if a database access error occurs.
   */
  private static void idempotentDeleteProduct(Connection conn, int id) throws SQLException {
    String deleteSql = "DELETE FROM PRODUCTS WHERE ID = ?;";
    try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
      pstmt.setInt(1, id);
      pstmt.executeUpdate();
      // Rows affected will be 1 if deleted, 0 if not found (already deleted or never existed).
      // The final state remains "product is absent" regardless of attempts.
    }
  }

  /**
   * Displays the details of a product by its ID.
   *
   * @param conn The database connection.
   * @param id The ID of the product to display.
   * @throws SQLException if a database access error occurs.
   */
  private static void displayProduct(Connection conn, int id) throws SQLException {
    String selectSql = "SELECT ID, NAME FROM PRODUCTS WHERE ID = ?;";
    try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
      pstmt.setInt(1, id);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          System.out.println(
              "Final State for Product ID "
                  + id
                  + ": ID: "
                  + rs.getInt("ID")
                  + ", Name: '"
                  + rs.getString("NAME")
                  + "'");
        } else {
          System.out.println(
              "Final State for Product ID " + id + ": Product with ID " + id + " not found.");
        }
      }
    }
  }
}
