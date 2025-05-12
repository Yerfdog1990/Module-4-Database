package org.learning;

/**
 * This class demonstrates different types of RowSet implementations in JDBC. It includes examples
 * of CachedRowSet, JdbcRowSet, and WebRowSet, showing their unique features and use cases.
 */
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import javax.sql.rowset.WebRowSet;

public class RowSetExamples {
  private static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
  private static final String USER = "root";
  private static final String PASSWORD = "";

  public static void main(String[] args) {
    try {
      initializeDatabase();
      demonstrateJdbcRowSet();
      demonstrateCachedRowSet();
      demonstrateWebRowSet();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void initializeDatabase() throws SQLException {
    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement stmt = conn.createStatement()) {
      stmt.execute(
          "CREATE TABLE IF NOT EXISTS students (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255))");
      stmt.execute("INSERT INTO students (name) VALUES ('John')");
      stmt.execute("INSERT INTO students (name) VALUES ('Jane')");
    }
  }

  private static void demonstrateJdbcRowSet() throws Exception {
    RowSetFactory factory = RowSetProvider.newFactory();
    JdbcRowSet jdbcRowSet = factory.createJdbcRowSet();

    jdbcRowSet.setUrl(URL);
    jdbcRowSet.setUsername(USER);
    jdbcRowSet.setPassword(PASSWORD);

    jdbcRowSet.setCommand("SELECT * FROM students");
    jdbcRowSet.execute();

    while (jdbcRowSet.next()) {
      System.out.println("JdbcRowSet: " + jdbcRowSet.getString("name"));
    }
    jdbcRowSet.close();
  }

  private static void demonstrateCachedRowSet() throws Exception {
    RowSetFactory factory = RowSetProvider.newFactory();
    CachedRowSet cachedRowSet = factory.createCachedRowSet();

    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {

      cachedRowSet.populate(rs);
    }

    while (cachedRowSet.next()) {
      System.out.println("CachedRowSet: " + cachedRowSet.getString("name"));
    }
    cachedRowSet.close();
  }

  private static void demonstrateWebRowSet() throws Exception {
    RowSetFactory factory = RowSetProvider.newFactory();
    WebRowSet webRowSet = factory.createWebRowSet();

    webRowSet.setUrl(URL);
    webRowSet.setUsername(USER);
    webRowSet.setPassword(PASSWORD);

    webRowSet.setCommand("SELECT * FROM students");
    webRowSet.execute();

    while (webRowSet.next()) {
      System.out.println("WebRowSet: " + webRowSet.getString("name"));
    }

    // Reset cursor and write to XML file
    webRowSet.beforeFirst();
    try (FileWriter writer = new FileWriter("students.xml")) {
      webRowSet.writeXml(writer);
    }

    webRowSet.close();
  }
}
