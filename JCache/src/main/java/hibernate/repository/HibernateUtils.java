package hibernate.repository;

import hibernate.model.SomeEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.function.Consumer;
import java.util.function.Function;

public final class HibernateUtils {
    private static SessionFactory sessionFactory;
    private HibernateUtils(){}
    public static SessionFactory getSessionFactory() {
        try{
            if(sessionFactory == null){
                sessionFactory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(SomeEntity.class).buildSessionFactory();
            }
            return sessionFactory;
        }catch(Exception e){
            System.err.println("Failed to create sessionFactory object." + e);
            throw new ExceptionInInitializerError(e);
        }
    }
    private static Session getSession(){
        return getSessionFactory().openSession();
    }
    private static void runInTransaction(final Consumer<Session> consumer){
        Function<Session, Void> fx = (session -> {
            consumer.accept(session);
            return null;
        });
        runInTransaction(getSession(), fx);
    }
    private static <T> T runInTransaction(final Session session, final Function<Session, T> fx){
        Transaction tx = session.beginTransaction();
        T result;
        try(session){
            result = fx.apply(session);
            tx.commit();
            return result;
        }
    }
    public static <T> T runInTransaction(final Function<Session, T> fx){
        return runInTransaction(getSession(), fx);
    }
}
