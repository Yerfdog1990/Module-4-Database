package hibernate.repository;

import hibernate.model.Document;
import java.util.function.Function;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class hibernateUtils {
  private static SessionFactory sessionFactory;

  public static SessionFactory getSessionFactory() {
    try {
      if (sessionFactory == null) {
        sessionFactory =
            new org.hibernate.cfg.Configuration()
                .configure("hibernate.cfg.xml").addAnnotatedClass(Document.class)
                .buildSessionFactory();
      }
      return sessionFactory;
    } catch (Exception e) {
      System.err.println("Failed to create sessionFactory object." + e);
      throw new ExceptionInInitializerError(e);
    }
  }

  public static Session getSession() {
    return getSessionFactory().openSession();
  }

  public static <T> T doWithSession(Function<Session, T> callback) {
    Session session = getSession();
    Transaction tx = session.beginTransaction();
    try {
      T result = callback.apply(session);
      tx.commit();
      return result;
    } catch (RuntimeException e) {
      tx.rollback();
      throw e;
    } finally {
      session.close();
    }
  }
}
