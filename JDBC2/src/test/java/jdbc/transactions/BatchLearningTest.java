package jdbc.transactions;

import static jdbc.transactions.JDBCUtil.getConnection;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BatchLearningTest {
  private int[] batchExecutionResult;

  @BeforeEach
  void setUp() throws SQLException {
    String createTableSQL =
        "CREATE TABLE EMPLOYEES (ID INT PRIMARY KEY, NAME VARCHAR(255), DEPARTMENT VARCHAR (255))";
    String insertDataSQL = "INSERT INTO EMPLOYEES VALUES (?, ?, ?)";

    JDBCUtil.standAloneRun(
        connection -> {
          connection.createStatement().execute(createTableSQL);
          PreparedStatement preparedStatement = connection.prepareStatement(insertDataSQL);
          preparedStatement.setInt(1, 1);
          preparedStatement.setString(2, "John");
          preparedStatement.setString(3, "IT");
          preparedStatement.addBatch();
          preparedStatement.setInt(1, 2);
          preparedStatement.setString(2, "Mary");
          preparedStatement.setString(3, "HR");
          preparedStatement.addBatch();
          batchExecutionResult = preparedStatement.executeBatch();
        });
  }

  @Test
  void learningBatch() throws SQLException {
    // Check that the batch was executed
    assertEquals(2, batchExecutionResult.length);

    // Check that all rows were inserted
    assertEquals(1, batchExecutionResult[0]);
    assertEquals(1, batchExecutionResult[1]);

    // Check that we have 2 rows in the table
    assertEquals(2, findRowCount());
    System.out.println("Batch execution successful");
  }

  private int findRowCount() throws SQLException {
    String sql = "SELECT COUNT(*) FROM EMPLOYEES";
    Connection connection = getConnection();
    ResultSet resultSet = connection.createStatement().executeQuery(sql);
    resultSet.next();
    return resultSet.getInt(1);
  }
}
