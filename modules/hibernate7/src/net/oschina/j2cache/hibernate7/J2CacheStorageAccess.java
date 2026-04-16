package net.oschina.j2cache.hibernate7;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.util.JavaSerializer;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.IOException;
import java.util.Base64;

class J2CacheStorageAccess implements DomainDataStorageAccess {

    private final CacheChannel cacheChannel;
    private final String regionName;
    private final JavaSerializer keySerializer = new JavaSerializer();

    J2CacheStorageAccess(CacheChannel cacheChannel, String regionName) {
        this.cacheChannel = cacheChannel;
        this.regionName = regionName;
    }

    @Override
    public Object getFromCache(Object key, SharedSessionContractImplementor session) {
        CacheObject cacheObject = cacheChannel.get(regionName, normalizeKey(key), false);
        return cacheObject.rawValue();
    }

    @Override
    public void putIntoCache(Object key, Object value, SharedSessionContractImplementor session) {
        cacheChannel.set(regionName, normalizeKey(key), value, false);
    }

    @Override
    public boolean contains(Object key) {
        return cacheChannel.exists(regionName, normalizeKey(key));
    }

    @Override
    public void evictData() {
        cacheChannel.clear(regionName);
    }

    @Override
    public void evictData(Object key) {
        cacheChannel.evict(regionName, normalizeKey(key));
    }

    @Override
    public void release() {
    }

    private String normalizeKey(Object key) {
        try {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(keySerializer.serialize(key));
        } catch (IOException ex) {
            throw new CacheException("Unable to serialize Hibernate cache key", ex);
        }
    }
}
