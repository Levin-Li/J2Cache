package net.oschina.j2cache.hibernate.regions;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.hibernate.strategy.NonstrictReadWriteJ2CacheNaturalIdRegionAccessStrategy;
import net.oschina.j2cache.hibernate.strategy.ReadOnlyJ2CacheNaturalIdRegionAccessStrategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cfg.Settings;

/**
 */
public class J2CacheNaturalIdRegion extends J2CacheTransactionalDataRegion implements NaturalIdRegion {

    public J2CacheNaturalIdRegion(String name, CacheChannel cache, Settings settings, CacheDataDescription metadata){
        super(name, cache, settings, metadata);
    }

    @Override
    public NaturalIdRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        switch (accessType) {
            case READ_ONLY:
                return new ReadOnlyJ2CacheNaturalIdRegionAccessStrategy(this);
            case NONSTRICT_READ_WRITE:
                return new NonstrictReadWriteJ2CacheNaturalIdRegionAccessStrategy(this);
            default:
                throw new CacheException("Unsupported access strategy : " + accessType + ".");
        }
    }
}
