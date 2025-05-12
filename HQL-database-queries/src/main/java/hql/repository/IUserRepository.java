package hql.repository;

import hibernate.model.UserHQL;

public interface IUserRepository {
  void save(UserHQL user);

  UserHQL findById(int id);

  void update(UserHQL user);

  void delete(UserHQL user);
}
