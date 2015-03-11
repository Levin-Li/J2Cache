package net.oschina.j2cache.hibernate;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.TransactionalDataRegion;
import org.hibernate.cache.access.SoftLock;

/**
 * Abstract RegionAccessStrategy class J2Cache
 * @author honganan
 * @date   2015年3月11日22:59:40
 *
 */
public class AbstractJ2CacheRegionAccessStrategy<T extends TransactionalDataRegion> {

	protected T region;
	
	
	public AbstractJ2CacheRegionAccessStrategy(T region) {
		this.region = region;
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

	
}
