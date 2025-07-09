package com.hibernate.dao;

import com.hibernate.domain.Staff;
import org.hibernate.SessionFactory;

public class StaffDao extends GenericDao<Staff>{
    public StaffDao(SessionFactory sessionFactory) {
        super(Staff.class, sessionFactory);
    }
}
