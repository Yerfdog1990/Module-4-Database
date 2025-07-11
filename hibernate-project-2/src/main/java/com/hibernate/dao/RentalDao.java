package com.hibernate.dao;

import com.hibernate.domain.Rental;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class RentalDao extends GenericDao<Rental>{
    public RentalDao(SessionFactory sessionFactory) {
        super(Rental.class, sessionFactory);
    }

    public Rental getAnyReturnedRental() {
        Query<Rental> query = getCurrentSession().createQuery("select  r from Rental r where r.returnDate is null", Rental.class);
        query.setMaxResults(1);
        List<Rental> rentals = query.getResultList();
        return rentals.isEmpty() ? null : rentals.get(0);
    }
}
