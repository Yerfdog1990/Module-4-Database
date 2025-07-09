package com.hibernate.dao;

import com.hibernate.domain.FilmText;
import org.hibernate.SessionFactory;

public class FilmTextDao extends GenericDao<FilmText>{
    public FilmTextDao(SessionFactory sessionFactory) {
        super(FilmText.class, sessionFactory);
    }
}
