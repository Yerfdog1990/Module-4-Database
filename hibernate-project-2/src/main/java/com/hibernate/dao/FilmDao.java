package com.hibernate.dao;

import com.hibernate.domain.Film;
import org.hibernate.SessionFactory;

public class FilmDao extends GenericDao<Film>{
    public FilmDao(SessionFactory sessionFactory) {
        super(Film.class, sessionFactory);
    }
}
