package org.example.repository;

import org.example.model.Person;

public interface IRepository {
  // CRUD operations
  void create(Person user);

  Person read(int id);

  void update(Person user);

  void delete(int id);

  // Other operations
  Person getAllUser();

  Person getAllUser(Person users);

  Person getUserByUsername(String username);

  Person getUserByEmail(String email);

  Person getUserByPhone(String phone);

  Person getUserByAddress(String address);

  Person getUserByCitizenship(String citizenship);

  int getCount();
}
