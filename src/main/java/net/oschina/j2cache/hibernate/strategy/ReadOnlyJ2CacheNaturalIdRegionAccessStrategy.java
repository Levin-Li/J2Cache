package net.oschina.j2cache.hibernate.strategy;

import net.oschina.j2cache.hibernate.regions.J2CacheNaturalIdRegion;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy
 */
public class ReadOnlyJ2CacheNaturalIdRegionAccessStrategy extends AbstractJ2CacheNaturalIdRegionAccessStrategy {

    private Logger log = LoggerFactory.getLogger(ReadOnlyJ2CacheNaturalIdRegionAccessStrategy.class);

    public ReadOnlyJ2CacheNaturalIdRegionAccessStrategy(J2CacheNaturalIdRegion naturalIdRegion){
        super(naturalIdRegion);
    }

    @Override
    public boolean insert(Object key, Object value) throws CacheException {
        log.debug("region access strategy readonly naturalId insert() {} {}", getInternalRegion().getName(), key);
        return false;
    }

    @Override
    public boolean afterInsert(Object key, Object value) throws CacheException {
        log.debug("region access strategy readonly naturalId afterInsert() {} {}", getInternalRegion().getName(), key);
        return false;
    }

    @Override
    public boolean update(Object key, Object value) throws CacheException {
        log.debug("region access strategy readonly naturalId update() {} {}", getInternalRegion().getName(), key);
        throw new UnsupportedOperationException("ReadOnly strategy does not support update.");
    }

    @Override
    public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
        log.debug("region access strategy readonly naturalId afterUpdate() {} {}", getInternalRegion().getName(), key);
        throw new UnsupportedOperationException("ReadOnly strategy does not support update.");
    }
}
