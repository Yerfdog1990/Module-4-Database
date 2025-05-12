package hql.repository;

import hibernate.model.UserHQL;

public class UserRepositoryImpl implements IUserRepository {
  @Override
  public void save(UserHQL user) {
    HQLUtil.doWithSession(
        session -> {
          session.persist(user);
          return user;
        });
  }

  @Override
  public UserHQL findById(int id) {
    return HQLUtil.doWithSession(session -> session.get(UserHQL.class, id));
  }

  @Override
  public void update(UserHQL user) {
    HQLUtil.doWithSession(
        session -> {
          session.merge(user);
          return user;
        });
  }

  @Override
  public void delete(UserHQL user) {
    HQLUtil.doWithSession(
        session -> {
          session.remove(user);
          return null;
        });
  }
}
