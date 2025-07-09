package com.hibernate.dao;

import com.hibernate.domain.Rental;
import org.hibernate.SessionFactory;

public class RentalDao extends GenericDao<Rental>{
    public RentalDao(SessionFactory sessionFactory) {
        super(Rental.class, sessionFactory);
    }
}
