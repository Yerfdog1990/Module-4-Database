package jdbc.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegularTransactionTest {
  @BeforeEach
  void setUp() throws SQLException {
    MyConsumer consumer =
        connection -> {
          connection
              .createStatement()
              .execute("CREATE TABLE ACCOUNT (ID INT PRIMARY KEY, BALANCE DECIMAL(10,2))");
          connection
              .createStatement()
              .execute("INSERT INTO ACCOUNT VALUES (1, 500), (2, 200), (3, 800)");
        };
    JDBCUtil.standAloneRun(consumer);
  }

  @AfterEach
  void tearDown() throws SQLException {
    JDBCUtil.standAloneRun(
        connection -> connection.createStatement().execute("DROP TABLE ACCOUNT"));
  }

  @Test
  void learningCommit() throws SQLException {
    JDBCUtil.standAloneRunWithTransaction(
        connection -> {
          connection
              .createStatement()
              .execute("UPDATE ACCOUNT SET BALANCE = BALANCE - 100 WHERE ID = 1");
          connection
              .createStatement()
              .execute("UPDATE ACCOUNT SET BALANCE = BALANCE + 100 WHERE ID = 2");
          connection.commit();
          System.out.println("Transaction successful");
        });
    printBalances();
    double getBalanceAccount1 = findBalance(1);
    double getBalanceAccount2 = findBalance(2);
    assertEquals(400, getBalanceAccount1);
    assertEquals(300, getBalanceAccount2);
  }

  @Test
  void learningRollback() throws SQLException {
    SQLException exception =
        assertThrows(
            SQLException.class,
            () ->
                JDBCUtil.standAloneRunWithTransaction(
                    connection -> {
                      Statement statement = connection.createStatement();
                      statement.execute("UPDATE ACCOUNT SET BALANCE = BALANCE - 100 WHERE ID = 1");
                      simulateDBError();
                      statement.execute("UPDATE ACCOUNT SET BALANCE = BALANCE + 100 WHERE ID = 2");
                      System.out.println("Transaction successful");
                    }));
    double getBalanceAccount1 = findBalance(1);
    double getBalanceAccount2 = findBalance(2);
    assertEquals(500, getBalanceAccount1);
    assertEquals(200, getBalanceAccount2);
    printBalances();
  }

  private static void simulateDBError() throws SQLException {
    throw new SQLException("Transaction failed");
  }

  private static double findBalance(int accountId) throws SQLException {
    Connection connection = JDBCUtil.getConnection();
    ResultSet rs =
        connection
            .createStatement()
            .executeQuery("SELECT BALANCE FROM ACCOUNT WHERE ID = " + accountId);

    if (rs.next()) {
      return rs.getDouble(1);
    } else return -1;
  }

  private static void printBalances() throws SQLException {
    Connection c = JDBCUtil.getConnection();
    ResultSet rs = c.createStatement().executeQuery("SELECT * FROM ACCOUNT");
    while (rs.next()) {
      System.out.println(rs.getInt(1) + " -> " + rs.getBigDecimal(2));
    }
  }
}
