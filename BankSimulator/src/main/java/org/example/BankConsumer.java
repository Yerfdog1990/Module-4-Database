package org.example;

import java.sql.Connection;

@FunctionalInterface
public interface BankConsumer {
    void accept(Connection connection);
}