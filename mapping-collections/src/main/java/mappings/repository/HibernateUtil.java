package mappings.repository;

import mappings.model.onetoone.ExclusiveEmployeeUni;
import mappings.model.onetoone.ExclusiveTaskUni;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.function.Function;

public class HibernateUtil {
  private static SessionFactory sessionFactory;
  private static Class<?>[] getEntities() {
    return new Class<?>[] {
            mappings.model.temporalannotation.Meeting.class,
            mappings.model.temporalannotation.Event.class,
            mappings.model.enumannotation.Worker.class,
            mappings.model.enumannotation.WorkerType.class,
            mappings.model.formulaanotation.BMICalculator.class,
            mappings.model.embeddedannotation.Citizenship.class,
            mappings.model.typeannotation.Employee.class,
            mappings.model.collections.Users.class,
            ExclusiveEmployeeUni.class,
            ExclusiveTaskUni.class,
    };
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
    try {
      if (sessionFactory == null) {
        sessionFactory = getConfiguration().buildSessionFactory();
      }
    } catch (Throwable ex) {
      System.err.println("Initial SessionFactory creation failed." + ex);
      throw new ExceptionInInitializerError(ex);
    }
    return sessionFactory;
  }

  // Open session
  private static Session getSession() {
    return getSessionFactory().openSession();
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
