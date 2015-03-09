package net.oschina.j2cache;

import java.util.Map;

import org.hibernate.cache.CacheException;

public class J2Cache implements org.hibernate.cache.Cache {

	CacheChannel cacheChannel = CacheChannel.getInstance();

	@Override
	public Object read(Object key) throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(Object key) throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(Object key, Object value) throws CacheException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Object key, Object value) throws CacheException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Object key) throws CacheException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() throws CacheException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() throws CacheException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lock(Object key) throws CacheException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unlock(Object key) throws CacheException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long nextTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getRegionName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSizeInMemory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getElementCountInMemory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getElementCountOnDisk() {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map toMap() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
