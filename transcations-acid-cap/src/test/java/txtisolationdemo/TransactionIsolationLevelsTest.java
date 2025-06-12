package txtisolationdemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static txisolationdemo.JdbcUtils.setUpDatabase;

import java.sql.Connection;
import org.junit.jupiter.api.Test;
import txisolationdemo.JdbcUtils;

public class TransactionIsolationLevelsTest {

  // 1. Dirty Reads:
  //    - Definition: A dirty read occurs when a transaction reads uncommitted changes made by
  // another transaction. These changes might later be rolled back, leading to potential
  // inconsistencies.
  //    - Isolation Level to Allow Dirty Reads: `READ_UNCOMMITTED`
  //    - Isolation Level to Prevent Dirty Reads: Any higher isolation level (`READ_COMMITTED`,
  // `REPEATABLE_READ,` or `SERIALIZABLE`) will prevent dirty reads.
  @Test
  void dirtyReadTest() {
    setUpDatabase();

    // First transaction -> read balance
    // In a first transaction, we read the balance of the account
    Connection conn1 = JdbcUtils.createConnection(Connection.TRANSACTION_READ_UNCOMMITTED);
    Integer balanceInTx1 = JdbcUtils.queryBalanceInConnection(conn1);
    // Then we open a new Connection/transaction and update that balance
    Connection conn2 = JdbcUtils.createConnection(Connection.TRANSACTION_READ_UNCOMMITTED);
    JdbcUtils.updateBalanceInConnection(50, conn2);
    // While the first transaction is still open, we reread the balance of the account (b2).
    Integer newBalanceRead = JdbcUtils.queryBalanceInConnection(conn1);
    // We will get a different result from the previous read
    // In the first transaction (still open), we reread the balance of the account (b2)
    // We will get a different result from the previous read
    assertNotEquals(balanceInTx1, newBalanceRead);
    System.out.printf("First read: %d%n", balanceInTx1);
    System.out.printf("Second read: %d%n", newBalanceRead);
  }

  // 2. Non-Repeatable Reads:
  //    - Definition: A non-repeatable read happens when a transaction reads the same row twice
  // and gets different values because another transaction has modified (and committed) the row in
  // between the two reads.
  //    - Isolation Level to Allow Non-Repeatable Reads: `READ_COMMITTED`
  //    - Isolation Level to Prevent Non-Repeatable Reads: `REPEATABLE_READ` or `SERIALIZABLE`
  @Test
  void nonRepeatableReadTest() {
    setUpDatabase();
    Connection conn1 = JdbcUtils.createConnection(Connection.TRANSACTION_READ_COMMITTED);
    Integer balanceInTx1 = JdbcUtils.queryBalanceInConnection(conn1);
    Connection conn2 = JdbcUtils.createConnection(Connection.TRANSACTION_READ_COMMITTED);
    JdbcUtils.updateBalanceInConnection(50, conn2);
    Integer newBalanceRead = JdbcUtils.queryBalanceInConnection(conn1);
    assertEquals(balanceInTx1, newBalanceRead);
    System.out.printf("First read: %d%n", balanceInTx1);
    System.out.printf("Second read: %d%n", newBalanceRead);
  }

  // 3. Phantom Reads:
  //    - Definition: A phantom read occurs when a transaction executes the same query twice and
  // retrieves different rows because another transaction inserted or deleted rows in the
  // intervening period.
  //    - Isolation Level to Allow Phantom Reads: `REPEATABLE_READ`
  //    - Isolation Level to Prevent Phantom Reads: `SERIALIZABLE`
  @Test
  void phantomReadTest() {
    setUpDatabase();
    Connection conn1 = JdbcUtils.createConnection(Connection.TRANSACTION_REPEATABLE_READ);
    Integer balanceInTx1 = JdbcUtils.queryBalanceInConnection(conn1);
    Connection conn2 = JdbcUtils.createConnection(Connection.TRANSACTION_REPEATABLE_READ);
    JdbcUtils.updateBalanceInConnection(50, conn2);
    Integer newBalanceRead = JdbcUtils.queryBalanceInConnection(conn1);
    assertEquals(balanceInTx1, newBalanceRead);
    System.out.printf("First read: %d%n", balanceInTx1);
    System.out.printf("Second read: %d%n", newBalanceRead);
  }

  // 4. Lost Updates (a Side Effect of Weak Isolation):
  //    - Definition: Lost updates occur when two transactions overwrite each other’s changes
  // because no proper locking or synchronization mechanism is enforced.
  //    - Isolation Level to Prevent Lost Updates: In practice, lost updates are typically mitigated
  // using explicit locking, optimistic/pessimistic concurrency control, or `SERIALIZABLE`
  // isolation.

  @Test
  void lostUpdateTest() {
    setUpDatabase();
    Connection conn1 = JdbcUtils.createConnection(Connection.TRANSACTION_SERIALIZABLE);
    Integer balanceInTx1 = JdbcUtils.queryBalanceInConnection(conn1);
    Connection conn2 = JdbcUtils.createConnection(Connection.TRANSACTION_SERIALIZABLE);
    JdbcUtils.updateBalanceInConnection(50, conn2);
    Integer newBalanceRead = JdbcUtils.queryBalanceInConnection(conn1);
    assertEquals(balanceInTx1, newBalanceRead);
    System.out.printf("First read: %d%n", balanceInTx1);
    System.out.printf("Second read: %d%n", newBalanceRead);
  }
}
