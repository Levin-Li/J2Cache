package net.oschina.j2cache.hibernate.strategy;

import net.oschina.j2cache.hibernate.regions.J2CacheEntityRegion;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy
 */
public class ReadOnlyEntityRegionAccessStrategy extends AbstractJ2CacheEntityRegionAccessStrategy {

    private Logger log = LoggerFactory.getLogger(ReadOnlyEntityRegionAccessStrategy.class);

    public ReadOnlyEntityRegionAccessStrategy(J2CacheEntityRegion entityRegion){
        super(entityRegion);
    }

    @Override
    public boolean insert(Object key, Object value, Object version) throws CacheException {
        log.debug("region access strategy readonly entity insert() {} {}", getInternalRegion().getName(), key);
        return false;
    }

    @Override
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
        log.debug("region access strategy readonly entity afterInsert() {} {}", getInternalRegion().getName(), key);
        return false;
    }

    @Override
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
                                                                                                  throws CacheException {
        log.debug("region access strategy readonly entity update() {} {}", getInternalRegion().getName(), key);
        throw new UnsupportedOperationException("ReadOnly strategy does not support update.");
    }

    @Override
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
                                                                                                                      throws CacheException {
        log.debug("region access strategy readonly entity afterUpdate() {} {}", getInternalRegion().getName(), key);
        throw new UnsupportedOperationException("ReadOnly strategy does not support update.");
    }
}
