package com.hibernate.dao;

import com.hibernate.domain.Customer;
import org.hibernate.SessionFactory;

public class CustomerDao extends GenericDao<Customer>{
    public CustomerDao(SessionFactory sessionFactory) {
        super(Customer.class, sessionFactory);
    }
}
