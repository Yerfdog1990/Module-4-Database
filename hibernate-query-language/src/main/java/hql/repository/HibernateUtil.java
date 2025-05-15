package hql.repository;

import hql.model.SchoolUser;
import java.util.function.Function;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
  private static SessionFactory sessionFactory;

  // Create a session factory configure Hibernate
  public static SessionFactory getSessionFactory() {
    try {
      if (sessionFactory == null) {
        sessionFactory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(SchoolUser.class).buildSessionFactory();
      }
      return sessionFactory;
    } catch (Throwable ex) {
      System.err.println("Initial SessionFactory creation failed." + ex);
      throw new ExceptionInInitializerError(ex);
    }
  }

  // Open session
  public static Session getSession() {
    return getSessionFactory().openSession();
  }

  // Begin the transaction, execute the callback, commit or rollback, and close the session.
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
