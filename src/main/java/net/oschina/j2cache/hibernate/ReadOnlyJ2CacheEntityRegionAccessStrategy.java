package net.oschina.j2cache.hibernate;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.EntityRegion;
import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.cache.access.SoftLock;
import org.hibernate.cfg.Settings;

public class ReadOnlyJ2CacheEntityRegionAccessStrategy implements EntityRegionAccessStrategy {

	private final EntityRegion region;
	
	private final Settings settings;
	
	
	
	public ReadOnlyJ2CacheEntityRegionAccessStrategy(EntityRegion region,
			Settings settings) {
		this.region = region;
		this.settings = settings;
	}

	@Override
	public EntityRegion getRegion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(Object key, long txTimestamp) throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean putFromLoad(Object key, Object value, long txTimestamp,
			Object version) throws CacheException {
		return putFromLoad(key, value, txTimestamp, version, settings.isMinimalPutsEnabled());
	}

	@Override
	public boolean putFromLoad(Object key, Object value, long txTimestamp,
			Object version, boolean minimalPutOverride) throws CacheException {
		/*if (minimalPutOverride && region.contains(key)) {
            return false;
        } else {
            region.put(key, value);
            return true;
        }*/
		return false;
	}

	@Override
	public SoftLock lockItem(Object key, Object version) throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SoftLock lockRegion() throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unlockItem(Object key, SoftLock lock) throws CacheException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unlockRegion(SoftLock lock) throws CacheException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean insert(Object key, Object value, Object version)
			throws CacheException {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean afterUpdate(Object key, Object value, Object currentVersion,
			Object previousVersion, SoftLock lock) throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void remove(Object key) throws CacheException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAll() throws CacheException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evict(Object key) throws CacheException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evictAll() throws CacheException {
		// TODO Auto-generated method stub
		
	}

}
