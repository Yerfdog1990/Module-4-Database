package com.hibernate.dao;

import com.hibernate.domain.Store;
import org.hibernate.SessionFactory;

public class StoreDao extends GenericDao<Store>{
    public StoreDao(SessionFactory sessionFactory) {
        super(Store.class, sessionFactory);
    }
}
