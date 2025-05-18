package org.example.repository;

import java.util.function.Function;
import org.example.model.Person;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class HibernateUtil {
  private static SessionFactory sessionFactory;

  // Configure hibernate.cfg.xml
  public static SessionFactory getSessionFactory() {
    try {
      if (sessionFactory == null) {
        sessionFactory =
            new org.hibernate.cfg.Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Person.class)
                .buildSessionFactory();
      }
    } catch (Throwable ex) {
      System.err.println("Initial SessionFactory creation failed." + ex);
      throw new ExceptionInInitializerError(ex);
    }
    return sessionFactory;
  }

  // Open session
  public static Session getSession() {
    return getSessionFactory().openSession();
  }

  // Begin transaction
  public static <T> T doWIthSession(Function<Session, T> rollback) {
    Session session = getSession();
    Transaction transaction = session.beginTransaction();
    try {
      T result = rollback.apply(session);
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
