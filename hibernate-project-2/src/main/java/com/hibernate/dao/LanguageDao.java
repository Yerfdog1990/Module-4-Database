package com.hibernate.dao;

import com.hibernate.domain.Language;
import org.hibernate.SessionFactory;

public class LanguageDao extends GenericDao<Language>{
    public LanguageDao(SessionFactory sessionFactory) {
        super(Language.class, sessionFactory);
    }
}
