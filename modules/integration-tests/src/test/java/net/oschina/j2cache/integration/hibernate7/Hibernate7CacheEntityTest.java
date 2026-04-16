package net.oschina.j2cache.integration.hibernate7;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Hibernate7CacheEntityTest {

    @Test
    void secondLevelCacheHitsOnSecondLoad() throws Exception {
        try (SessionFactory sessionFactory = Hibernate7SessionFactoryHelper.buildSessionFactory()) {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                session.persist(new Hibernate7CacheEntity(1L, "demo"));
                session.getTransaction().commit();
            }

            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                Hibernate7CacheEntity entity = session.find(Hibernate7CacheEntity.class, 1L);
                assertEquals("demo", entity.getName());
                session.getTransaction().commit();
            }

            Statistics statistics = sessionFactory.getStatistics();
            statistics.clear();

            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                Hibernate7CacheEntity entity = session.find(Hibernate7CacheEntity.class, 1L);
                assertEquals("demo", entity.getName());
                session.getTransaction().commit();
            }

            assertEquals(1, statistics.getSecondLevelCacheHitCount());
        }
    }
}
