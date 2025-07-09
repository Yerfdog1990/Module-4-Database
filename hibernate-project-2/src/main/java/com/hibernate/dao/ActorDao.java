package com.hibernate.dao;

import com.hibernate.domain.Actor;
import org.hibernate.SessionFactory;

public class ActorDao extends GenericDao<Actor>{
    public ActorDao(SessionFactory sessionFactory) {
        super(Actor.class, sessionFactory);
    }
}
