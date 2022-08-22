package ru.javawebinar.topjava.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class JpaUtil {

    @PersistenceContext
    private EntityManager em;

    public void clear2ndLevelHibernateCache() {
//        Session s = (Session) em.getDelegate();
//        SessionFactory sf = s.getSessionFactory();
//        sf.getCache().evictEntityData(User.class, AbstractBaseEntity.START_SEQ);
//        sf.getCache().evictEntityData(User.class);
        getSessionFactory().getCache().evictAllRegions();
    }

    public boolean isHibernateSecondLevelCache() {
        return "true".equals(getSessionFactory().getProperties().get("hibernate.cache.use_second_level_cache"));
    }

    private SessionFactory getSessionFactory() {
        return ((Session) em.getDelegate()).getSessionFactory();
    }
}
