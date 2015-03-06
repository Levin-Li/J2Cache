package net.oschina.j2cache.hibernate.strategy;

import net.oschina.j2cache.hibernate.regions.J2CacheEntityRegion;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class NonstrictReadWriteJ2CacheEntityRegionAccessStrategy extends AbstractJ2CacheEntityRegionAccessStrategy {

    private Logger log = LoggerFactory.getLogger(NonstrictReadWriteJ2CacheEntityRegionAccessStrategy.class);

    public NonstrictReadWriteJ2CacheEntityRegionAccessStrategy(J2CacheEntityRegion entityRegion){
        super(entityRegion);
    }

    @Override
    public boolean insert(Object key, Object value, Object version) throws CacheException {
        log.debug("region access strategy nonstrict-read-write entity insert() {} {}",
            getInternalRegion().getName(),
            key);
        return false;
    }

    @Override
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
        log.debug("region access strategy nonstrict-read-write entity afterInsert() {} {}",
            getInternalRegion().getName(),
            key);
        return false;
    }

    /**
     * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy
     */
    @Override
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
                                                                                                  throws CacheException {
        log.debug("region access strategy nonstrict-read-write entity update() {} {}",
            getInternalRegion().getName(),
            key);
        return false;
    }

    /**
     * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy
     */
    @Override
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
                                                                                                                      throws CacheException {
        log.debug("region access strategy nonstrict-read-write entity afterUpdate() {} {}",
            getInternalRegion().getName(),
            key);
        getInternalRegion().evict(key);
        return false;
    }
}
