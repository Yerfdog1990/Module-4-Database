package util;

import static org.hibernate.cfg.AvailableSettings.*;

import entities.Comment;
import entities.Employee;
import entities.SomeEntity;
import entities.User;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.sql.DataSource;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.Cache;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.H2Dialect;

public final class HibernateUtil {

  private static final SessionFactory sessionFactory = buildSessionFactory();

  public static void runInTransaction(final Consumer<Session> consumer) {
    Function<Session, Void> fx =
        ((session1) -> {
          consumer.accept(session1);
          return null;
        });
    runInTransaction(getSession(), fx);
  }

  private static  <T> T runInTransaction(final Session session, final Function<Session, T> function) {
    Transaction transaction = session.beginTransaction();
    T result;
    try (session) {
      result = function.apply(session);
      transaction.commit();
      return result;
    }
  }

  public static  <T> T runInTransaction(final Function<Session, T> function) {
    return runInTransaction(getSession(), function);
  }

  private static Session getSession() {
    return sessionFactory.openSession();
  }

  private static SessionFactory buildSessionFactory() {
    try {
      // Configure the session factory
      final Map<String, Object> settings = new HashMap<>();
      settings.put(DATASOURCE, buildProxyDataSource());
      settings.put(HBM2DDL_AUTO, "create-drop");
      settings.put(DIALECT, H2Dialect.class.getName());
      settings.put(AUTOCOMMIT, false);
      settings.put(USE_SECOND_LEVEL_CACHE, true);
      settings.put(CACHE_REGION_FACTORY, "org.hibernate.cache.ehcache.EhCacheRegionFactory");
      settings.put(USE_QUERY_CACHE, true);

      final StandardServiceRegistryBuilder standardRegistryBuilder =
          new StandardServiceRegistryBuilder();
      standardRegistryBuilder.applySettings(settings);

      final StandardServiceRegistry standardRegistry = standardRegistryBuilder.build();

      final MetadataSources metadataSources =
          new MetadataSources(standardRegistry)
              .addAnnotatedClass(SomeEntity.class)
              .addAnnotatedClass(User.class)
              .addAnnotatedClass(Comment.class)
              .addAnnotatedClass(Employee.class);

      final Metadata metadata = metadataSources.getMetadataBuilder().build();
      return metadata.buildSessionFactory();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static DataSource buildProxyDataSource() {
    return ProxyDataSourceBuilder.create(buildDataSource())
        .name("ProxyDataSource")
        .countQuery()
        .build();
  }

  private static DataSource buildDataSource() {
    final JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1");
    return dataSource;
  }

  private Cache getCache() {
    return sessionFactory.getCache();
  }

  public void evictEntity(Class<?> type) {
    getCache().evict(type);
  }

  private QueryCount getQueriesStats() {
    return QueryCountHolder.getGrandTotal();
  }

  public long getNumOfSelects() {
    return getQueriesStats().getSelect();
  }

  public long getNumOfInserts() {
    return getQueriesStats().getInsert();
  }
}
