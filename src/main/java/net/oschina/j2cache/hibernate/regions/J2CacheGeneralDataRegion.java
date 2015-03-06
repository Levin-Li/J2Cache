package net.oschina.j2cache.hibernate.regions;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.GeneralDataRegion;
import org.hibernate.cfg.Settings;

public class J2CacheGeneralDataRegion extends J2CacheRegion implements GeneralDataRegion {

    public J2CacheGeneralDataRegion(String name, CacheChannel cache, Settings settings){
        super(name, cache, settings);
    }

    @Override
    public Object get(Object key) throws CacheException {
        if (key == null) {
            return null;
        } else {
            try {
                CacheObject cacheObject = cache.get(getName(), key);
                return cacheObject == null ? null : cacheObject.getValue();
            } catch (net.oschina.j2cache.CacheException e) {
                throw new CacheException(e);
            }
        }
    }

    @Override
    public void put(Object key, Object value) throws CacheException {
        try {
            cache.set(getName(), key, value);
        } catch (net.oschina.j2cache.CacheException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void evict(Object key) throws CacheException {
        cache.evict(getName(), key);
    }

    @Override
    public void evictAll() throws CacheException {
        cache.clear(getName());
    }
}
