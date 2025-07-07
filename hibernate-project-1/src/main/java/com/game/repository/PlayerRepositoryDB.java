package com.game.repository;

import com.game.entity.Player;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.annotation.PreDestroy;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

  private final SessionFactory sessionFactory;

  public PlayerRepositoryDB() {
    Properties properties = new Properties();
    properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
    properties.put(Environment.JAKARTA_JDBC_DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
    properties.put(Environment.JAKARTA_JDBC_URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
    properties.put(Environment.JAKARTA_JDBC_USER, "root");
    properties.put(Environment.JAKARTA_JDBC_PASSWORD, "Cyril@2019");
    properties.put(Environment.SHOW_SQL, "true");
    properties.put(Environment.HBM2DDL_AUTO, "update");
    properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
    properties.put(Environment.USE_SQL_COMMENTS, "true");
    properties.put(Environment.FORMAT_SQL, "true");
    properties.put(Environment.USE_GET_GENERATED_KEYS, "true");

    sessionFactory =
        new Configuration()
            .addAnnotatedClass(Player.class)
            .addProperties(properties)
            .buildSessionFactory();
  }

  @Override
  public List<Player> getAll(int pageNumber, int pageSize) {
    try (Session session = sessionFactory.openSession()) {
      NativeQuery<Player> nativeQuery =
          session.createNativeQuery("select * from rpg.player", Player.class);
      nativeQuery.setFirstResult(pageNumber * pageSize);
      nativeQuery.setMaxResults(pageSize);
      return nativeQuery.list();
    }
  }

  @Override
  public int getAllCount() {
    try (Session session = sessionFactory.openSession()) {
      Long count = session.createNamedQuery("player_getAllCount", Long.class).getResultCount();
      return count != null ? count.intValue() : 0;
    }
  }

  @Override
  public Player save(Player player) {
    try (Session session = sessionFactory.openSession()) {
      session.beginTransaction();
      session.persist(player);
      session.getTransaction().commit();
      return player;
    }
  }

  @Override
  public Player update(Player player) {
    try (Session session = sessionFactory.openSession()) {
      session.beginTransaction();
      session.update(player);
      session.getTransaction().commit();
      return player;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<Player> findById(long id) {
    try (Session session = sessionFactory.openSession()) {
      Player player = session.get(Player.class, id);
      return Optional.of(player);
    }
  }

  @Override
  public void delete(Player player) {
    try (Session session = sessionFactory.openSession()) {
      session.beginTransaction();
      session.delete(player);
      session.getTransaction().commit();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @PreDestroy
  public void beforeStop() {
    if (sessionFactory != null) {
      sessionFactory.close();
    }
  }
}
