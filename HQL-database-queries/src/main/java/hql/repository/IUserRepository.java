package hql.hql.repository;

import hibernate.model.UserHibernate;

public interface IUserRepository {
  void save(UserHibernate user);

  UserHibernate findById(int id);

  void update(UserHibernate user);

  void delete(UserHibernate user);
}
