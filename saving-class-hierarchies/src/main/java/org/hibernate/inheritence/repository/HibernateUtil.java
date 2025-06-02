package org.hibernate.inheritence.repository;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.inheritence.model.strategy.singletable.Animal;
import org.hibernate.inheritence.model.strategy.singletable.Cat;
import org.hibernate.inheritence.model.strategy.singletable.Dog;
import org.hibernate.inheritence.model.strategy.tableperclass.Person;
import org.hibernate.inheritence.model.strategy.tableperclass.Peter;
import org.hibernate.inheritence.model.strategy.tableperclass.John;
import org.hibernate.inheritence.model.strategy.mappedsupperclass.BMW;
import org.hibernate.inheritence.model.strategy.mappedsupperclass.Benz;
import org.hibernate.inheritence.model.strategy.mappedsupperclass.Car;
import org.hibernate.inheritence.model.strategy.joinedtable.Room1;
import org.hibernate.inheritence.model.strategy.joinedtable.Room2;
import org.hibernate.inheritence.model.strategy.joinedtable.House;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HibernateUtil {
  private static SessionFactory sessionFactory;
  private static Class<?>[] getEntities() {
    return new Class<?>[] {
            Animal.class,
            Cat.class,
            Dog.class,
            Person.class,
            Peter.class,
            John.class,
            BMW.class,
            Benz.class,
            Car.class,
            House.class,
            Room1.class,
            Room2.class
    };
  }
private static DataSource createDataSource() {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:hibernate-orm-demo;DB_CLOSE_DELAY=-1;");
    return ProxyDataSourceBuilder.create(dataSource)
            .name("H2 DataSource")
            .countQuery()
            .build();
}
  private static SessionFactory createDataSourceProxySessionFactory() {
    Map<String, Object> settings = getStringObjectMap();

    StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
    registryBuilder.applySettings(settings);

    StandardServiceRegistry serviceRegistry = registryBuilder.build();
    MetadataSources metadataSources = new MetadataSources(serviceRegistry);
    metadataSources.addAnnotatedClasses(Dog.class, Cat.class, Animal.class, Person.class, Peter.class, John.class, BMW.class, Benz.class, Car.class, House.class, Room1.class, Room2.class);


    Metadata metadata = metadataSources.getMetadataBuilder().build();
    return metadata.buildSessionFactory();
  }

  private static Map<String, Object> getStringObjectMap() {
    Map<String, Object> settings = new HashMap<>();
    settings.put(AvailableSettings.DATASOURCE, createDataSource());
    settings.put(AvailableSettings.HBM2DDL_AUTO, "create-drop");
    settings.put(AvailableSettings.SHOW_SQL, "true");
    settings.put(AvailableSettings.FORMAT_SQL, "true");
    settings.put(AvailableSettings.USE_SQL_COMMENTS, "true");
    // settings.put(AvailableSettings.DIALECT, "org.hibernate.dialect.H2Dialect");
    settings.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");
    settings.put(AvailableSettings.STATEMENT_BATCH_SIZE, "50");
    settings.put(AvailableSettings.ORDER_UPDATES, "true");
    settings.put(AvailableSettings.USE_SECOND_LEVEL_CACHE, "false");
    settings.put(AvailableSettings.USE_QUERY_CACHE, "false");
    settings.put(AvailableSettings.AUTOCOMMIT, "false");
    return settings;
  }

  private static Configuration getConfiguration() {
    Configuration configuration = new Configuration();
    // Database connection settings
    configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
    configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:testdb");
    configuration.setProperty("hibernate.connection.username", "sa");
    configuration.setProperty("hibernate.connection.password", "");

    // H2 Dialect
    configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

    // Connection Pool Configuration
    configuration.setProperty("hibernate.connection.pool_size", "10");
    configuration.setProperty("hibernate.current_session_context_class", "thread");

    // SQL Logging
    configuration.setProperty("hibernate.show_sql", "true");
    configuration.setProperty("hibernate.format_sql", "true");

    // Schema Generation
    configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");

    // Batch Size Configuration
    configuration.setProperty("hibernate.jdbc.batch_size", "50");
    configuration.setProperty("hibernate.order_updates", "true");

    for(Class<?> entity : getEntities()){
      configuration.addAnnotatedClass(entity);
    }
    return configuration;
  }
  // Configure hibernate
  public static SessionFactory getSessionFactory() {
    if (sessionFactory == null) {
      sessionFactory = createDataSourceProxySessionFactory();
    }
    return sessionFactory;
  }

  // Open session
  private static Session getSession() {
    return getSessionFactory().unwrap(SessionFactory.class).openSession();
  }

  // Begin transaction
  public static <T> T doWithSession(Function<Session, T> callback) {
    Session session = getSession();
    Transaction transaction = session.beginTransaction();
    try {
      T result = callback.apply(session);
      transaction.commit();
      return result;
    } catch (RuntimeException e) {
      transaction.rollback();
      throw e;
    } finally {
      session.close();
    }
  }
}
