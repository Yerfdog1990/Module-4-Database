package com.hibernate.dao;

import com.hibernate.domain.Inventory;
import org.hibernate.SessionFactory;

public class InventoryDao extends GenericDao<Inventory>{
    public InventoryDao(SessionFactory sessionFactory) {
        super(Inventory.class, sessionFactory);
    }
}
