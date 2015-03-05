package net.oschina.j2cache.hibernate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.oschina.j2cache.CacheChannel;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.Timestamper;

/**
 * J2Cache plugin for Hibernate
 * 
 * @version <pre>
 * Author	Version		Date		Changes
 * liuye 	1.0  		2015年3月5日 	Created
 *
 * </pre>
 * @since 1.
 */
@SuppressWarnings("deprecation")
public class J2Cache implements Cache {

    private String       regionName;
    private CacheChannel cache;

    public J2Cache(String name, CacheChannel cache){
        this.regionName = name;
        this.cache = cache;
    }

    @Override
    public Object read(Object key) throws CacheException {
        return get(key);
    }

    @Override
    public Object get(Object key) throws CacheException {
        if (key == null) {
            return null;
        } else {
            try {
                return cache.get(getRegionName(), key);
            } catch (net.oschina.j2cache.CacheException e) {
                throw new CacheException(e);
            }
        }
    }

    @Override
    public void put(Object key, Object value) throws CacheException {
        try {
            cache.set(getRegionName(), key, value);
        } catch (net.oschina.j2cache.CacheException e) {
            throw new CacheException(e);
        }

    }

    @Override
    public void update(Object key, Object value) throws CacheException {
        put(key, value);
    }

    @Override
    public void remove(Object key) throws CacheException {
        try {
            cache.evict(getRegionName(), key);
        } catch (net.oschina.j2cache.CacheException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void clear() throws CacheException {
        try {
            cache.clear(getRegionName());
        } catch (net.oschina.j2cache.CacheException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void destroy() throws CacheException {
    }

    @Override
    public void lock(Object key) throws CacheException {
    }

    @Override
    public void unlock(Object key) throws CacheException {
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    public int getTimeout() {
        return -1;
    }

    @Override
    public String getRegionName() {
        return this.regionName;
    }

    @Override
    public long getSizeInMemory() {
        return -1;
    }

    @Override
    public long getElementCountInMemory() {
        return -1;
    }

    @Override
    public long getElementCountOnDisk() {
        return -1;
    }

    @Override
    public Map toMap() {
        try {
            Map<Object, Object> result = new HashMap<Object, Object>();
            List keys = cache.keys(getRegionName());
            if (keys != null) {
                Iterator iter = keys.iterator();
                while (iter.hasNext()) {
                    Object key = iter.next();
                    result.put(key, cache.get(getRegionName(), key));
                }
            }
            return result;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public String toString() {
        return "J2Cache(" + getRegionName() + ')';
    }

}
