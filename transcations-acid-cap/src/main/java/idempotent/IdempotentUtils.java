package idempotent;

import java.sql.*;
import java.util.function.Consumer;

public class IdempotentUtils {

  private static Connection doWithConnection(Consumer<Connection> consumer) throws SQLException {
    // H2 Database URL for an in-memory database.
    // DB_CLOSE_DELAY=-1 prevents the database from closing when the last connection is closed.
    String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    String USER = "sa";
    String PASS = "";
    Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
    consumer.accept(conn);
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        System.err.println("Error closing connection: " + e.getMessage());
      }
    }
    return conn;
  }

  /**
   * Creates the PRODUCTS table if it does not already exist.
   *
   * @throws SQLException if a database access error occurs.
   */
  public static void createTable() throws SQLException {
    doWithConnection(
        connection -> {
          String createTableSql =
              "CREATE TABLE IF NOT EXISTS PRODUCTS (ID INT PRIMARY KEY, NAME VARCHAR(255));";
          try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSql);
            System.out.println("Created PRODUCTS table.");
          } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
          }
        });
  }

  /**
   * This method performs an UPSERT (Update or Insert) operation, making it an idempotent operation.
   * Using H2's MERGE INTO statement for concise UPSERT.
   *
   * @param id The ID of the product.
   * @param name The name of the product.
   * @throws SQLException if a database access error occurs.
   */
  public static void upsertProduct(int id, String name) throws SQLException {
    doWithConnection(
        connection -> {
          // MERGE INTO attempts to update a row if a matching primary key (ID) exists,
          // otherwise, it inserts a new row.
          String mergeSql = "MERGE INTO PRODUCTS KEY(ID) VALUES (?, ?);";
          try (PreparedStatement pstmt = connection.prepareStatement(mergeSql)) {
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
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
  }

  /**
   * Deletes a product by ID. This is also an idempotent operation. Deleting an already deleted item
   * has the same effect as deleting it once.
   *
   * @param id The ID of the product to delete.
   * @throws SQLException if a database access error occurs.
   */
  public static void deleteProduct(int id) throws SQLException {
    doWithConnection(
        connection -> {
          String deleteSql = "DELETE FROM PRODUCTS WHERE ID = ?;";
          try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
              System.out.println("Deleted product ID " + id + ".");
            } else {
              System.out.println(
                  "Product ID "
                      + id
                      + " was not found for deletion (already deleted or never existed).");
            }
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
  }

  /**
   * Displays the details of a product by its ID.
   *
   * @param id The ID of the product to display.
   * @return
   * @throws SQLException if a database access error occurs.
   */
  public static void displayProduct(int id) throws SQLException {
    doWithConnection(
        connection -> {
          String selectSql = "SELECT ID, NAME FROM PRODUCTS WHERE ID = ?;";
          try (PreparedStatement pstmt = connection.prepareStatement(selectSql)) {
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
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
  }
}
