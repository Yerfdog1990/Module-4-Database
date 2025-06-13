package idempotent;

import java.sql.*;

public class IdempotentWithIsolation {
  // H2 Database URL for an in-memory database.
  // DB_CLOSE_DELAY=-1 prevents the database from closing when the last connection is closed.
  private static final String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
  private static final String USER = "sa";
  private static final String PASS = "";

  public static void main(String[] args) {
    Connection conn = null;
    try {
      // Register the H2 JDBC driver (not strictly necessary for modern JDBC but good practice)
      Class.forName("org.h2.Driver");

      // Establish a connection to the H2 database
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      System.out.println("Connected to H2 in-memory database.");

      // 1. Create the 'PRODUCTS' table if it doesn't exist
      createTable(conn);

      // --- Demonstrating Idempotency ---

      // Scenario 1: First execution (INSERT)
      System.out.println("\n--- Scenario 1: First execution (Expected: INSERT) ---");
      upsertProduct(conn, 1, "Laptop");
      displayProduct(conn, 1); // Should show Laptop

      // Scenario 2: Second execution (UPDATE) - changing the name
      System.out.println("\n--- Scenario 2: Second execution (Expected: UPDATE) ---");
      upsertProduct(conn, 1, "Gaming Laptop");
      displayProduct(conn, 1); // Should show Gaming Laptop

      // Scenario 3: Third execution (No Change / UPDATE to same value) - demonstrating idempotency
      // Even though executed again, the state remains consistent.
      System.out.println(
          "\n--- Scenario 3: Third execution (Expected: Same state, Idempotent) ---");
      upsertProduct(conn, 1, "Gaming Laptop"); // Same data as last time
      displayProduct(conn, 1); // Should still show Gaming Laptop

      System.out.println("\n--- Scenario 4: Multiple identical calls (Idempotent) ---");
      // Call the idempotent operation multiple times with the exact same data
      upsertProduct(conn, 2, "Smartphone");
      upsertProduct(conn, 2, "Smartphone");
      upsertProduct(conn, 2, "Smartphone");
      System.out.println("Executed upsertProduct(2, \"Smartphone\") three times consecutively.");
      displayProduct(conn, 2); // Should show Smartphone, regardless of 1 or 3 calls.

      // Scenario 5: Idempotent Deletion (Optional, but also a good example)
      System.out.println("\n--- Scenario 5: Idempotent Deletion ---");
      deleteProduct(conn, 1);
      System.out.println("Attempted to delete product ID 1.");
      displayProduct(conn, 1); // Should show "Product with ID 1 not found."

      deleteProduct(conn, 1); // Delete again, it's still deleted.
      System.out.println("Attempted to delete product ID 1 again (idempotent).");
      displayProduct(conn, 1); // Should still show "Product with ID 1 not found."

    } catch (ClassNotFoundException e) {
      System.err.println("H2 JDBC Driver not found: " + e.getMessage());
    } catch (SQLException e) {
      System.err.println("SQL Error: " + e.getMessage());
      e.printStackTrace();
    } finally {
      // Close the connection
      try {
        if (conn != null) {
          conn.close();
          System.out.println("\nConnection closed.");
        }
      } catch (SQLException e) {
        System.err.println("Error closing connection: " + e.getMessage());
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
   * @param conn The database connection.
   * @param id The ID of the product.
   * @param name The name of the product.
   * @throws SQLException if a database access error occurs.
   */
  private static void upsertProduct(Connection conn, int id, String name) throws SQLException {
    // MERGE INTO attempts to update a row if a matching primary key (ID) exists,
    // otherwise, it inserts a new row.
    String mergeSql = "MERGE INTO PRODUCTS KEY(ID) VALUES (?, ?);";
    try (PreparedStatement pstmt = conn.prepareStatement(mergeSql)) {
      pstmt.setInt(1, id);
      pstmt.setString(2, name);
      int rowsAffected = pstmt.executeUpdate();
      if (rowsAffected > 0) {
        System.out.println(
            "Upserted product ID "
                + id
                + " with name '"
                + name
                + "'. Rows affected: "
                + rowsAffected);
      }
    }
  }

  /**
   * Deletes a product by ID. This is also an idempotent operation. Deleting an already deleted item
   * has the same effect as deleting it once.
   *
   * @param conn The database connection.
   * @param id The ID of the product to delete.
   * @throws SQLException if a database access error occurs.
   */
  private static void deleteProduct(Connection conn, int id) throws SQLException {
    String deleteSql = "DELETE FROM PRODUCTS WHERE ID = ?;";
    try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
      pstmt.setInt(1, id);
      int rowsAffected = pstmt.executeUpdate();
      if (rowsAffected > 0) {
        System.out.println("Deleted product ID " + id + ".");
      } else {
        System.out.println(
            "Product ID " + id + " was not found for deletion (already deleted or never existed).");
      }
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
              "Current State: Product ID: "
                  + rs.getInt("ID")
                  + ", Name: '"
                  + rs.getString("NAME")
                  + "'");
        } else {
          System.out.println("Product with ID " + id + " not found.");
        }
      }
    }
  }
}
