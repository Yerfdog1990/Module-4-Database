package hibernate.repository;

import hibernate.model.Customer;
import hibernate.model.PhoneNumber;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.function.Function;

public class HibernateUtils {
    private static SessionFactory sessionFactory;
    public static SessionFactory getSessionFactory() {
        try{
            if(sessionFactory == null){
                sessionFactory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Customer.class).buildSessionFactory();
            }
            return sessionFactory;
        }catch(Exception e){
            System.err.println("Failed to create sessionFactory object." + e);
            throw new ExceptionInInitializerError(e);
        }
    }
    public static Session getSession(){
        return getSessionFactory().openSession();
    }

    public static  <T> T doWithSession(Function<Session, T> function){
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try{
            T result = function.apply(session);
            transaction.commit();
            return result;
        }catch(Exception e){
            transaction.rollback();
            throw e;
        }finally{
            session.close();
        }
    }
}
