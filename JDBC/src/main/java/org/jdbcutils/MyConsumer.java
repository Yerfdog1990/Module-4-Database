package org.jdbcutils;

import java.sql.SQLException;

@FunctionalInterface
public interface MyConsumer<T> {
  public void accept(T t) throws SQLException;
}
