package hibernate.repository;

import java.util.function.Function;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class HibernateUtil {
  private static SessionFactory sessionFactory;

  // Configure hibernate.cfg.xml file
  public static SessionFactory getSessionFactory() {
    try {
      if (sessionFactory == null) {
        sessionFactory = new org.hibernate.cfg.Configuration().configure().buildSessionFactory();
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
