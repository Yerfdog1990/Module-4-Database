package hql.hql.repository;

import hibernate.model.UserHibernate;

public class UserRepositoryImpl implements IUserRepository {
  @Override
  public void save(UserHibernate user) {
    HQLUtil.doWithSession(
        session -> {
          session.persist(user);
          return user;
        });
  }

  @Override
  public UserHibernate findById(int id) {
    return HQLUtil.doWithSession(session -> session.get(UserHibernate.class, id));
  }

  @Override
  public void update(UserHibernate user) {
    HQLUtil.doWithSession(
        session -> {
          session.merge(user);
          return user;
        });
  }

  @Override
  public void delete(UserHibernate user) {
    HQLUtil.doWithSession(
        session -> {
          session.remove(user);
          return null;
        });
  }
}
