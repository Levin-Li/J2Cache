package net.oschina.j2cache.hibernate;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.EntityRegion;
import org.hibernate.cache.TransactionalDataRegion;
import org.hibernate.cache.access.SoftLock;

public class AbstractJ2CacheRegionAccessStrategy<T extends TransactionalDataRegion> {

	protected T region;
	
	
	public AbstractJ2CacheRegionAccessStrategy(T region) {
		this.region = region;
	}

	public EntityRegion getRegion() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object get(Object key, long txTimestamp) throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean putFromLoad(Object key, Object value, long txTimestamp,
			Object version) throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean putFromLoad(Object key, Object value, long txTimestamp,
			Object version, boolean minimalPutOverride) throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	
	public SoftLock lockItem(Object key, Object version) throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public SoftLock lockRegion() throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void unlockItem(Object key, SoftLock lock) throws CacheException {
		// TODO Auto-generated method stub

	}

	
	public void unlockRegion(SoftLock lock) throws CacheException {
		// TODO Auto-generated method stub

	}

	
	public boolean insert(Object key, Object value, Object version)
			throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean afterInsert(Object key, Object value, Object version)
			throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean update(Object key, Object value, Object currentVersion,
			Object previousVersion) throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean afterUpdate(Object key, Object value, Object currentVersion,
			Object previousVersion, SoftLock lock) throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void remove(Object key) throws CacheException {
		// TODO Auto-generated method stub

	}

	
	public void removeAll() throws CacheException {
		// TODO Auto-generated method stub

	}

	
	public void evict(Object key) throws CacheException {
		// TODO Auto-generated method stub

	}

	
	public void evictAll() throws CacheException {
		// TODO Auto-generated method stub

	}

}
