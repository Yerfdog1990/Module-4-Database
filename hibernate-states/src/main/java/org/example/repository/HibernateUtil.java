package org.example.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Function;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        try {
            if (sessionFactory == null) {
                sessionFactory = new org.hibernate.cfg.Configuration().configure().buildSessionFactory();
            }
            return sessionFactory;
        } catch (Exception e) {
            System.err.println("Initial SessionFactory creation failed." + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Session getSession() {
        return getSessionFactory().openSession();
    }

    public static <T> T doWithSession(Function<Session, T> rollback) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            T result = rollback.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}