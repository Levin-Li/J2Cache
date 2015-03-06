package net.oschina.j2cache.hibernate.regions;

import net.oschina.j2cache.CacheChannel;

import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link org.hibernate.cache.spi.QueryResultsRegion}
 */
public class J2CacheQueryResultsRegion extends J2CacheGeneralDataRegion implements QueryResultsRegion {

    private Logger log = LoggerFactory.getLogger(J2CacheQueryResultsRegion.class);

    public J2CacheQueryResultsRegion(String name, CacheChannel cache, Settings settings){
        super(name, cache, settings);
    }

}
