package hql.repository;

import hql.model.SchoolUser;

public class UserRepositoryImpl implements IUserRepository {

  // Implement CRUD operation methods
  @Override
  public void create(SchoolUser user) {
    HibernateUtil.doWithSession(
        session -> {
          session.persist(user);
          return user;
        });
  }

  @Override
  public SchoolUser read(SchoolUser user) {
    return HibernateUtil.doWithSession(
        session -> {
          SchoolUser foundUser = session.get(SchoolUser.class, user.getId());
          return foundUser;
        });
  }

  @Override
  public void update(SchoolUser user) {
    HibernateUtil.doWithSession(
        session -> {
          session.update(user);
          return user;
        });
  }

  @Override
  public void delete(SchoolUser user) {
    HibernateUtil.doWithSession(
        session -> {
          session.remove(user);
          return user;
        });
  }

  @Override
  public SchoolUser findById(int id) {
    return HibernateUtil.doWithSession(
        session ->
            session
                .createQuery("FROM SchoolUser WHERE id = :id", SchoolUser.class)
                .setParameter("id", id)
                .uniqueResult());
  }

  @Override
  public SchoolUser findByName(String name) {
    return HibernateUtil.doWithSession(
        session ->
            session
                .createQuery("FROM SchoolUser WHERE name = :name", SchoolUser.class)
                .setParameter("name", name)
                .uniqueResult());
  }

  @Override
  public SchoolUser findByEmail(String email) {
    return HibernateUtil.doWithSession(
        session ->
            session
                .createQuery("FROM SchoolUser WHERE email = :email", SchoolUser.class)
                .setParameter("email", email)
                .uniqueResult());
  }

  @Override
  public SchoolUser findByPhone(String phone) {
    return HibernateUtil.doWithSession(
        session ->
            session
                .createQuery("FROM SchoolUser WHERE phone = :phone", SchoolUser.class)
                .setParameter("phone", phone)
                .uniqueResult());
  }

  @Override
  public SchoolUser findByAddress(String address) {
    return HibernateUtil.doWithSession(
        session ->
            session
                .createQuery("FROM SchoolUser WHERE physicalAddress = :address", SchoolUser.class)
                .setParameter("address", address)
                .uniqueResult());
  }
}
