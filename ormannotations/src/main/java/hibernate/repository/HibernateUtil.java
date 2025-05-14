package hibernate.repository;

import java.util.function.Function;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Utility class for managing Hibernate sessions and transactions.
 * Provides centralized access to SessionFactory and helper methods for session management.
 */
public class HibernateUtil {
  private static SessionFactory sessionFactory;

  /**
   * Gets or creates a SessionFactory singleton instance.
   * @return The SessionFactory instance used for creating Hibernate sessions
   * @throws ExceptionInInitializerError if the SessionFactory creation fails
   */
  public static SessionFactory getSessionFactory() {
    try {
      if (sessionFactory == null) {
        sessionFactory = new org.hibernate.cfg.Configuration().configure().buildSessionFactory();
      }
      return sessionFactory;
    } catch (Throwable ex) {
      System.err.println("Initial SessionFactory creation failed." + ex);
      throw new ExceptionInInitializerError(ex);
    }
  }

  /**
   * Creates a new Hibernate Session.
   * @return A new Session instance from the SessionFactory
   */
  public static Session getSession() {
    return getSessionFactory().openSession();
  }

  /**
   * Executes a database operation within a managed session and transaction context.
   * Handles transaction commit/rollback and session closure automatically.
   *
   * @param callback The database operation to execute
   * @param <T> The type of the result returned by the callback
   * @return The result of the callback execution
   * @throws RuntimeException if the database operation fails
   */
  public static <T> T doWithSession(Function<Session, T> callback) {
    Session session = getSession();
    Transaction transaction = session.beginTransaction();
    try {
      T result = callback.apply(session);
      transaction.commit();
      return result;
    } catch (RuntimeException e) {
      if (transaction != null) transaction.rollback();
      throw e;
    } finally {
      session.close();
    }
  }
}
