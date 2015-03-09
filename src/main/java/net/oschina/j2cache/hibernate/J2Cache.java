package net.oschina.j2cache.hibernate;

import java.util.Map;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;

import org.hibernate.cache.CacheException;

@SuppressWarnings("deprecation")
public class J2Cache implements org.hibernate.cache.Cache {

	private CacheChannel cacheChannel;
	private String REGION_NAME = "defaltRegion";
	
	public J2Cache() { }
	
	public J2Cache(String regionName, CacheChannel cacheChannel) {
		this.REGION_NAME = regionName;
		this.cacheChannel = cacheChannel;
	}
	

	@Override
	public Object read(Object key) throws CacheException {
		return get(key);
	}

	@Override
	public Object get(Object key) throws CacheException {
		CacheObject cacheObject = cacheChannel.get(REGION_NAME, key);
		if(cacheObject!=null) {
			return cacheObject.getValue();
		}
		return key;
	}

	@Override
	public void put(Object key, Object value) throws CacheException {
		cacheChannel.set(REGION_NAME, key, value);
	}

	@Override
	public void update(Object key, Object value) throws CacheException {
		cacheChannel.set(REGION_NAME, key, value);
		
	}

	@Override
	public void remove(Object key) throws CacheException {
		cacheChannel.evict(REGION_NAME, key);
		
	}

	@Override
	public void clear() throws CacheException {
		cacheChannel.clear(REGION_NAME);
	}

	@Override
	public void destroy() throws CacheException {
		cacheChannel.close();
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
		return this.REGION_NAME;
	}

	@Override
	public long getSizeInMemory() {
		return -1;
	}

	@Override
	public long getElementCountInMemory() {
		return cacheChannel.keys(this.REGION_NAME).size();
	}

	@Override
	public long getElementCountOnDisk() {
		return 0;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map toMap() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
