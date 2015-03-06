package net.oschina.j2cache.hibernate.strategy;

import net.oschina.j2cache.hibernate.regions.J2CacheNaturalIdRegion;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class NonstrictReadWriteJ2CacheNaturalIdRegionAccessStrategy extends AbstractJ2CacheNaturalIdRegionAccessStrategy {

    private Logger log = LoggerFactory.getLogger(NonstrictReadWriteJ2CacheNaturalIdRegionAccessStrategy.class);

    public NonstrictReadWriteJ2CacheNaturalIdRegionAccessStrategy(J2CacheNaturalIdRegion naturalIdRegion){
        super(naturalIdRegion);
    }

    @Override
    public boolean insert(Object key, Object value) throws CacheException {
        log.debug("region access strategy nonstrict-read-write naturalId insert() {} {}",
            getInternalRegion().getName(),
            key);
        return false;
    }

    @Override
    public boolean afterInsert(Object key, Object value) throws CacheException {
        log.debug("region access strategy nonstrict-read-write naturalId afterInsert() {} {}",
            getInternalRegion().getName(),
            key);
        return false;
    }

    /**
     * @see org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy
     */
    @Override
    public boolean update(Object key, Object value) throws CacheException {
        log.debug("region access strategy nonstrict-read-write naturalId update() {} {}",
            getInternalRegion().getName(),
            key);
        return false;
    }

    /**
     * @see org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy
     */
    @Override
    public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
        log.debug("region access strategy nonstrict-read-write naturalId afterUpdate() {} {}",
            getInternalRegion().getName(),
            key);
        getInternalRegion().evict(key);
        return false;
    }
}
