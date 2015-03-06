/**
 * 
 */
package net.oschina.j2cache.hibernate;

import java.util.Properties;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.hibernate.regions.J2CacheCollectionRegion;
import net.oschina.j2cache.hibernate.regions.J2CacheEntityRegion;
import net.oschina.j2cache.hibernate.regions.J2CacheNaturalIdRegion;
import net.oschina.j2cache.hibernate.regions.J2CacheQueryResultsRegion;
import net.oschina.j2cache.hibernate.regions.J2CacheTimestampRegion;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;
import org.hibernate.testing.cache.Timestamper;

/**
 * J2Cache Hibernate RegionFactory implementations.
 * 
 * @author steven.liu
 */
public class J2CacheRegionFactory implements RegionFactory {

    private Settings     settings;
    private CacheChannel channel = CacheChannel.getInstance();

    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return true;
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    public void start(Settings settings, Properties properties) throws CacheException {
        this.settings = settings;
    }

    @Override
    public void stop() {
        channel.close();
    }

    @Override
    public AccessType getDefaultAccessType() {
        return AccessType.NONSTRICT_READ_WRITE;
    }

    @Override
    public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) {
        return new J2CacheEntityRegion(regionName, channel, settings, metadata);
    }

    @Override
    public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata) {
        return new J2CacheNaturalIdRegion(regionName, channel, settings, metadata);
    }

    @Override
    public CollectionRegion buildCollectionRegion(String regionName, Properties properties,
                                                  CacheDataDescription metadata) throws CacheException {
        return new J2CacheCollectionRegion(regionName, channel, settings, metadata);
    }

    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
        return new J2CacheQueryResultsRegion(regionName, channel, settings);
    }

    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
        return new J2CacheTimestampRegion(regionName, channel, settings);
    }
}
