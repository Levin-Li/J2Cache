package net.oschina.j2cache.hibernate.regions;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.hibernate.strategy.NonstrictReadWriteJ2CacheCollectionRegionAccessStrategy;
import net.oschina.j2cache.hibernate.strategy.ReadOnlyJ2CacheCollectionRegionAccessStrategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cfg.Settings;

public class J2CacheCollectionRegion extends J2CacheTransactionalDataRegion implements CollectionRegion {

    public J2CacheCollectionRegion(String name, CacheChannel cache, Settings settings, CacheDataDescription metadata){
        super(name, cache, settings, metadata);
    }

    @Override
    public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        switch (accessType) {
            case READ_ONLY:
                return new ReadOnlyJ2CacheCollectionRegionAccessStrategy(this);
            case NONSTRICT_READ_WRITE:
                return new NonstrictReadWriteJ2CacheCollectionRegionAccessStrategy(this);
            default:
                throw new CacheException("Unsupported access strategy : " + accessType + ".");
        }
    }
}
