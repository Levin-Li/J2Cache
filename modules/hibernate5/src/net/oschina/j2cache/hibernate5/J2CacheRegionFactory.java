package net.oschina.j2cache.hibernate5;

import java.util.Properties;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;

import net.oschina.j2cache.J2Cache;

public class J2CacheRegionFactory extends AbstractJ2CacheRegionFactory {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3645607098856367186L;

	@SuppressWarnings("unused")
	private static final String SPRING_CACHEMANAGER = "hibernate.cache.spring.cache_manager";
	@SuppressWarnings("unused")
    private static final String DEFAULT_SPRING_CACHEMANAGER = "cacheManager";

    public J2CacheRegionFactory() {
    }

    public J2CacheRegionFactory(Properties prop) {
        super();
    }

    @Override
    public void start(SessionFactoryOptions settings, Properties properties) throws CacheException {
        this.settings = settings;
        if (this.channel == null) {
            this.channel = J2Cache.getChannel();
        }
    }

    @Override
    public void stop() {
        if (channel != null) {
            channel.close();
        }
    }
}
