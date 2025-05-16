package org.example.repository;

import org.example.model.Person;

public abstract class RepositoryImpl implements IRepository {
  @Override
  public void create(Person user) {
    HibernateUtil.doWIthSession(
        session -> {
          session.persist(user);
          return user;
        });
  }

  @Override
  public Person read(int id) {
    return HibernateUtil.doWIthSession(
        session -> {
          Person foundUser = session.get(Person.class, id);
          return foundUser;
        });
  }

  @Override
  public void update(Person user) {
    HibernateUtil.doWIthSession(
        session -> {
          session.update(user);
          return user;
        });
  }

  @Override
  public void delete(int id) {
    HibernateUtil.doWIthSession(
        session -> {
          Person foundUser = session.get(Person.class, id);
          session.delete(foundUser);
          return foundUser;
        });
  }

  @Override
  public Person getAllUser(Person users) {
    return HibernateUtil.doWIthSession(
        session -> {
          session.createQuery("from Person", Person.class);
          return users;
        });
  }

  @Override
  public Person getUserByUsername(String username) {
    return HibernateUtil.doWIthSession(
        session -> {
          Person foundUser = session.get(Person.class, username);
          return foundUser;
        });
  }

  @Override
  public Person getUserByEmail(String email) {
    return HibernateUtil.doWIthSession(
        session -> {
          Person foundUser = session.get(Person.class, email);
          return foundUser;
        });
  }

  @Override
  public Person getUserByPhone(String phone) {
    return HibernateUtil.doWIthSession(
        session -> {
          Person foundUser = session.get(Person.class, phone);
          return foundUser;
        });
  }

  @Override
  public Person getUserByAddress(String address) {
    return HibernateUtil.doWIthSession(
        session -> {
          Person foundUser = session.get(Person.class, address);
          return foundUser;
        });
  }

  @Override
  public Person getUserByCitizenship(String citizenship) {
    return HibernateUtil.doWIthSession(
        session -> {
          Person foundUser = session.get(Person.class, citizenship);
          return foundUser;
        });
  }

  @Override
  public int getCount() {
    return HibernateUtil.doWIthSession(
        session -> {
          return ((Long) session.createQuery("select count(*) from Person").getSingleResult())
              .intValue();
        });
  }
}
