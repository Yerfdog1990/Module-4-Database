package org.example;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface MyConsumer {
  void accept(Connection connection) throws SQLException;
}
