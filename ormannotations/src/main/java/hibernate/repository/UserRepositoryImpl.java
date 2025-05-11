package hibernate.repository;

import hibernate.model.UserHibernate;

public class UserRepositoryImpl implements IUserRepository {
  @Override
  public void save(UserHibernate user) {
    HibernateUtil.doWithSession(
        session -> {
          session.persist(user);
          return user;
        });
  }

  @Override
  public UserHibernate findById(int id) {
    return HibernateUtil.doWithSession(session -> session.get(UserHibernate.class, id));
  }

  @Override
  public void update(UserHibernate user) {
    HibernateUtil.doWithSession(
        session -> {
          session.merge(user);
          return user;
        });
  }

  @Override
  public void delete(UserHibernate user) {
    HibernateUtil.doWithSession(
        session -> {
          session.remove(user);
          return null;
        });
  }
}
