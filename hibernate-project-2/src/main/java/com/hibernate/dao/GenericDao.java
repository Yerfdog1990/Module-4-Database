package com.hibernate.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class GenericDao<T> {
    private final Class<T> clazz;
    private SessionFactory sessionFactory;
    public GenericDao(Class<T> clazzSet, SessionFactory sessionFactory) {
        this.clazz = clazzSet;
        this.sessionFactory = sessionFactory;
    }
    public T getById(final int id){
        return (T) sessionFactory.getCurrentSession().get(clazz, id);
    }

    public List<T> getItems(int offset, int count){
        Query query = sessionFactory.getCurrentSession().createQuery("from " + clazz.getName());
        query.setFirstResult(offset);
        query.setMaxResults(count);
        return query.getResultList();
    }

    public List<T> findAll(){
        return sessionFactory.getCurrentSession().createQuery("from " + clazz.getName()).list();
    }
    public T save(final T entity){
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }
    public T update(final T entity){
        return (T) sessionFactory.getCurrentSession().merge(entity);
    }
    public void delete(final T entity){
        sessionFactory.getCurrentSession().delete(entity);
    }

    public void deleteById(final int entityId){
        final T entity = getById(entityId);
        if(entity != null){
            delete(entity);
        }
    }

    protected Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }
}
