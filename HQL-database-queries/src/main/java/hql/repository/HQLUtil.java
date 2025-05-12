package hql.repository;

import java.util.function.Function;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class HQLUtil {
  private static SessionFactory sessionFactory;

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

  public static Session getSession() {
    return getSessionFactory().openSession();
  }

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
