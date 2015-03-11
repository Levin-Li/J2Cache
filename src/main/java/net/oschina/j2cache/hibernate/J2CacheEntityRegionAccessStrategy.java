package net.oschina.j2cache.hibernate;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.EntityRegion;
import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.cache.access.SoftLock;

/**
 * 
 * Region Access strategy for entity
 * @author honganan
 * @date   2015年3月11日22:59:40
 *
 */
public class J2CacheEntityRegionAccessStrategy extends AbstractJ2CacheRegionAccessStrategy<J2CacheRegion.Entity> implements EntityRegionAccessStrategy {

	
	public J2CacheEntityRegionAccessStrategy(J2CacheRegion.Entity region) {
		super(region);
	}

	@Override
	public EntityRegion getRegion() {
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
	public boolean insert(Object key, Object value, Object version)
			throws CacheException {
		region.put(key, value);
		return true;
	}

	@Override
	public boolean afterInsert(Object key, Object value, Object version)
			throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Object key, Object value, Object currentVersion,
			Object previousVersion) throws CacheException {
		region.put(key, value);
		return true;
	}

	@Override
	public boolean afterUpdate(Object key, Object value, Object currentVersion,
			Object previousVersion, SoftLock lock) throws CacheException {
		// TODO Auto-generated method stub
		return false;
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
