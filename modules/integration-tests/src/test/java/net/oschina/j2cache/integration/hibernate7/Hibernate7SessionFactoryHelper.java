package net.oschina.j2cache.integration.hibernate7;

import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;
import net.oschina.j2cache.hibernate7.J2CacheRegionFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public final class Hibernate7SessionFactoryHelper {

    private Hibernate7SessionFactoryHelper() {
    }

    static SessionFactory buildSessionFactory() throws Exception {
        J2CacheConfig config = J2CacheConfig.initFromConfig("/hibernate7/j2cache.properties");
        J2CacheBuilder.init(config).getChannel();

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.connection.url", "jdbc:h2:mem:j2cache-hibernate7;DB_CLOSE_DELAY=-1")
                .applySetting("hibernate.connection.username", "sa")
                .applySetting("hibernate.connection.password", "")
                .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                .applySetting("hibernate.cache.use_second_level_cache", "true")
                .applySetting("hibernate.cache.region.factory_class", J2CacheRegionFactory.class.getName())
                .applySetting("hibernate.generate_statistics", "true")
                .build();

        return new MetadataSources(registry)
                .addAnnotatedClass(Hibernate7CacheEntity.class)
                .buildMetadata()
                .buildSessionFactory();
    }
}
