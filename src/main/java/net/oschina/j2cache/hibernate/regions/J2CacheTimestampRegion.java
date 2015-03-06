package net.oschina.j2cache.hibernate.regions;

import net.oschina.j2cache.CacheChannel;

import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cfg.Settings;

/**
 * {@link org.hibernate.cache.spi.TimestampsRegion}
 */
public class J2CacheTimestampRegion extends J2CacheGeneralDataRegion implements TimestampsRegion {

    public J2CacheTimestampRegion(String name, CacheChannel cache, Settings settings){
        super(name, cache, settings);
    }
}
