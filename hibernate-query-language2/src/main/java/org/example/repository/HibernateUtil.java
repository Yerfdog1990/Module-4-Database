package org.example.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.function.Function;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    // Configure hibernate.cfg.xml file
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                sessionFactory = new Configuration().configure().buildSessionFactory();
            } catch (Throwable ex) {
                System.err.println("Failed to create sessionFactory object." + ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
        return sessionFactory;
    }
    // Create a session
    public static Session getSession(){
        return getSessionFactory().openSession();
    }
    // Begin a transaction
    public static <T> T doWithSession(Function<Session, T> rollBack){
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            T result = rollBack.apply(session);
            transaction.commit();
            return result;
        }catch (Exception e){
            transaction.rollback();
            throw e;
        }
    }
}
