package net.oschina.j2cache.hibernate.regions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.oschina.j2cache.CacheChannel;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.Region;
import org.hibernate.cfg.Settings;
import org.hibernate.testing.cache.Timestamper;

public abstract class J2CacheRegion implements Region {

    public static final int  UNKNOWN                           = -1;

    private static final int DEFAULT_CACHE_LOCK_TIMEOUT_MILLIS = 60 * 1000;

    protected Settings       settings;
    protected String         regionName;
    protected CacheChannel   cache;

    public J2CacheRegion(String name, CacheChannel cache, Settings settings){
        this.regionName = name;
        this.cache = cache;
        this.settings = settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.regionName;
    }

    public Settings getSettings() {
        return settings;
    }

    @Override
    public void destroy() throws CacheException {
        // do nothing. NEVER evict cache region!!
    }

    /**
     * Memcached does not support contains or exists operation.
     */
    @Override
    public boolean contains(Object key) {
        return false;
    }

    @Override
    public long getSizeInMemory() {
        return UNKNOWN;
    }

    @Override
    public long getElementCountInMemory() {
        return UNKNOWN;
    }

    @Override
    public long getElementCountOnDisk() {
        return UNKNOWN;
    }

    @Override
    public Map toMap() {
        try {
            Map<Object, Object> result = new HashMap<Object, Object>();
            List keys = cache.keys(getName());
            if (keys != null) {
                Iterator iter = keys.iterator();
                while (iter.hasNext()) {
                    Object key = iter.next();
                    result.put(key, cache.get(getName(), key));
                }
            }
            return result;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    public int getTimeout() {
        return DEFAULT_CACHE_LOCK_TIMEOUT_MILLIS;
    }
}
