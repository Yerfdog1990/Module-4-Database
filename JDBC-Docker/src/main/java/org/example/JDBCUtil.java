package org.example;

import java.sql.*;

public class JDBCUtil {
  public static void main(String[] args) throws SQLException {
    String connectionUrl =
        "jdbc:mysql://localhost:3306/school?allowPublicKeyRetrieval=true&useSSL=false";
    String userName = "user";
    String password = "root";

    try {
      // Test if the driver is available
      Class.forName("com.mysql.cj.jdbc.Driver");

      System.out.println("Attempting to connect to: " + connectionUrl);
      Connection conn = DriverManager.getConnection(connectionUrl, userName, password);
      System.out.println("Connection Successful");

      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("select * from students");
      ResultSetMetaData rsmd = rs.getMetaData();
      while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String birthday = rs.getString("birthday");
        // System.out.println("ID: " + id + ", Name: " + name + ", Birthday: " + birthday);
        System.out.println("=========================");
        int columnCount = rsmd.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
          String columnName = rsmd.getColumnName(i);
          String columnTypeName = rsmd.getColumnTypeName(i);
          boolean isAutoIncrement = rsmd.isAutoIncrement(i);
          System.out.println(columnName + " -> " + columnTypeName + " -> " + isAutoIncrement);
        }
      }

      conn.close();
    } catch (SQLException e) {
      System.out.println("SQL Error: " + e.getMessage());
      System.out.println("SQL State: " + e.getSQLState());
      System.out.println("Error Code: " + e.getErrorCode());
      throw e;
    } catch (ClassNotFoundException e) {
      System.out.println("MySQL JDBC Driver not found.");
      e.printStackTrace();
    }
  }
}
