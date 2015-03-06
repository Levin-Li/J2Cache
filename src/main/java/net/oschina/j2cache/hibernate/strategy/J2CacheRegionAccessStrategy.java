package net.oschina.j2cache.hibernate.strategy;

import net.oschina.j2cache.hibernate.regions.J2CacheGeneralDataRegion;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.RegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Region Access Strategy.
 */
public class J2CacheRegionAccessStrategy implements RegionAccessStrategy {

    private Logger                   log = LoggerFactory.getLogger(J2CacheRegionAccessStrategy.class);

    private J2CacheGeneralDataRegion internalRegion;

    public J2CacheRegionAccessStrategy(J2CacheGeneralDataRegion internalRegion){
        this.internalRegion = internalRegion;
    }

    protected J2CacheGeneralDataRegion getInternalRegion() {
        return internalRegion;
    }

    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        log.debug("region access strategy get() {} {}", getInternalRegion().getName(), key);
        return getInternalRegion().get(key);
    }

    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
        return putFromLoad(key, value, txTimestamp, version, internalRegion.getSettings().isMinimalPutsEnabled());
    }

    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
                                                                                                                      throws CacheException {
        log.debug("region access strategy putFromLoad() {} {}", getInternalRegion().getName(), key);
        if ((key == null) || (value == null)) {
            return false;
        }

        getInternalRegion().put(key, value);
        return true;
    }

    @Override
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        log.debug("region access strategy lockItem() {} {}", getInternalRegion().getName(), key);
        return null;
    }

    /**
     * Region locks are not supported.
     */
    @Override
    public SoftLock lockRegion() throws CacheException {
        log.debug("region access strategy lockRegion() {}", getInternalRegion().getName());
        return null;
    }

    /**
     * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy
     */
    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        log.debug("region access strategy unlockItem() {} {}", getInternalRegion().getName(), key);
    }

    /**
     * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy
     * @see org.hibernate.cache.spi.access.RegionAccessStrategy#unlockRegion(org.hibernate.cache.spi.access.SoftLock)
     */
    @Override
    public void unlockRegion(SoftLock lock) throws CacheException {
        log.debug("region access strategy unlockRegion lock() {} {}", getInternalRegion().getName(), lock);
        evictAll();
    }

    /**
     * @see org.hibernate.cache.spi.access.RegionAccessStrategy#remove(java.lang.Object)
     */
    @Override
    public void remove(Object key) throws CacheException {
        log.debug("region access strategy remove() {} {}", getInternalRegion().getName(), key);
        evict(key);
    }

    /**
     * @see org.hibernate.cache.spi.access.RegionAccessStrategy#removeAll()
     */
    @Override
    public void removeAll() throws CacheException {
        log.debug("region access strategy removeAll() {}", getInternalRegion().getName());
    }

    /**
     * @see org.hibernate.cache.spi.access.RegionAccessStrategy#evict(java.lang.Object)
     */
    @Override
    public void evict(Object key) throws CacheException {
        log.debug("region access strategy evict() {} {}", getInternalRegion().getName(), key);
        getInternalRegion().evict(key);
    }

    @Override
    public void evictAll() throws CacheException {
        log.debug("region access strategy evictAll() {}", getInternalRegion().getName());
        getInternalRegion().evictAll();
    }
}
