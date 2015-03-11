package net.oschina.j2cache.hibernate;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.CollectionRegion;
import org.hibernate.cache.access.CollectionRegionAccessStrategy;

/**
 * 
 * Region Access strategy for Collection
 * @author honganan
 * @date   2015年3月11日22:59:40
 *
 */
public class J2CacheCollectionRegionAccessStrategy extends AbstractJ2CacheRegionAccessStrategy<J2CacheRegion.Collection> implements CollectionRegionAccessStrategy {

	
	public J2CacheCollectionRegionAccessStrategy(J2CacheRegion.Collection region) {
		super(region);
	}

	@Override
	public CollectionRegion getRegion() {
		return region;
	}

	@Override
	public Object get(Object key, long txTimestamp) throws CacheException {
		return region.get(key);
	}

	@Override
	public boolean putFromLoad(Object key, Object value, long txTimestamp,
			Object version) throws CacheException {
		region.put(key, value);
		return true;
	}

	@Override
	public boolean putFromLoad(Object key, Object value, long txTimestamp,
			Object version, boolean minimalPutOverride) throws CacheException {
		if(minimalPutOverride && region.get(key)!=null)
			return false;
		
		region.put(key, value);
		return true;
	}
	
	@Override
	public void remove(Object key) throws CacheException {
		region.evict(key);
	}

	@Override
	public void removeAll() throws CacheException {
		region.evictAll();
	}

	@Override
	public void evict(Object key) throws CacheException {
		region.evict(key);
	}

	@Override
	public void evictAll() throws CacheException {
		region.evictAll();
	}

}
