package IdempotentOnlyTest;

import idempotent.IdempotentOnly;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class IdempotentOnlyTest {
  @BeforeAll
  static void setUp() throws SQLException {
    IdempotentOnly.createTable();
    // Scenario 1: First execution (INSERT)
    System.out.println("\n--- Scenario 1: First execution (Expected: INSERT) ---");
    IdempotentOnly.upsertProduct(1, "Laptop");
    IdempotentOnly.displayProduct(1); // Should show Laptop

    // Scenario 2: Second execution (UPDATE) - changing the name
    System.out.println("\n--- Scenario 2: Second execution (Expected: UPDATE) ---");
    IdempotentOnly.upsertProduct(1, "Gaming Laptop");
    IdempotentOnly.displayProduct(1); // Should show Gaming Laptop
  }

  @Test
  void demonstrateIdempotency() throws SQLException {
    // Scenario 3: Third execution (No Change / UPDATE to same value) - demonstrating idempotency
    // Even though executed again, the state remains consistent.
    System.out.println("\n--- Scenario 3: Third execution (Expected: Same state, Idempotent) ---");
    IdempotentOnly.upsertProduct(1, "Gaming Laptop"); // Same data as last time
    IdempotentOnly.displayProduct(1); // Should still show Gaming Laptop
  }

  @Test
  void multipleIdenticalCalls() throws SQLException {
    // Scenario 4: Multiple identical calls (Idempotent)
    System.out.println("\n--- Scenario 4: Multiple identical calls (Idempotent) ---");
    // Call the idempotent operation multiple times with the exact same data
    IdempotentOnly.upsertProduct(2, "Smartphone");
    IdempotentOnly.upsertProduct(2, "Smartphone");
    IdempotentOnly.upsertProduct(2, "Smartphone");
    System.out.println("Executed upsertProduct(2, \"Smartphone\") three times consecutively.");
    IdempotentOnly.displayProduct(2); // Should show Smartphone, regardless of 1 or 3 calls.
  }

  @Test
  void deleteData() throws SQLException {
    // Scenario 5: Idempotent Deletion (Optional, but also a good example)
    System.out.println("\n--- Scenario 5: Idempotent Deletion ---");
    IdempotentOnly.deleteProduct(1);
    System.out.println("Attempted to delete product ID 1.");
    IdempotentOnly.displayProduct(1); // Should show "Product with ID 1 not found."
    IdempotentOnly.deleteProduct(1); // Delete it again, it's still deleted.
    System.out.println("Attempted to delete product ID 1 again (idempotent).");
    IdempotentOnly.displayProduct(1); // Should still show "Product with ID 1 not found."
  }
}
