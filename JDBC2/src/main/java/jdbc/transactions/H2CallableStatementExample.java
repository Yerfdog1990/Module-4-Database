package jdbc.transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class H2CallableStatementExample {
  public static ResultSet getBalanceForAccount(Connection connection, int accountId)
      throws SQLException {
    String sql = "SELECT BALANCE FROM ACCOUNT WHERE ID = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1, accountId);
    return preparedStatement.executeQuery();
  }
}
