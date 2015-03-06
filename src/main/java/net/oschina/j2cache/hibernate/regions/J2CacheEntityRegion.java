package net.oschina.j2cache.hibernate.regions;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.hibernate.strategy.NonstrictReadWriteJ2CacheEntityRegionAccessStrategy;
import net.oschina.j2cache.hibernate.strategy.ReadOnlyEntityRegionAccessStrategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cfg.Settings;

public class J2CacheEntityRegion extends J2CacheTransactionalDataRegion implements EntityRegion {

    public J2CacheEntityRegion(String name, CacheChannel cache, Settings settings, CacheDataDescription metadata){
        super(name, cache, settings, metadata);
    }

    @Override
    public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        switch (accessType) {
            case READ_ONLY:
                return new ReadOnlyEntityRegionAccessStrategy(this);
            case NONSTRICT_READ_WRITE:
                return new NonstrictReadWriteJ2CacheEntityRegionAccessStrategy(this);
            default:
                throw new CacheException("Unsupported access strategy : " + accessType + ".");
        }
    }
}
