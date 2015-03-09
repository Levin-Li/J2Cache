package net.oschina.j2cache.hibernate3;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class J2CacheProvider implements CacheProvider {

	@Override
	public Cache buildCache(String regionName, Properties properties)
			throws CacheException {
		CacheChannel cl = CacheChannel.getInstance();
		return new J2Cache(cl,regionName);
	}

	@Override
	public long nextTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void start(Properties properties) throws CacheException {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isMinimalPutsEnabledByDefault() {
		// TODO Auto-generated method stub
		return false;
	}

	private static class J2Cache implements Cache {
		private static Logger logger = LoggerFactory.getLogger(J2Cache.class);
		
		private CacheChannel cl = null; 
		private String regionName = null;
		
		public J2Cache(CacheChannel cl, String regionName) {
			this.cl = cl;
			this.regionName = regionName;
		}
		
		@Override
		public Object read(Object key) throws CacheException {
			return get(key);
		}

		@Override
		public Object get(Object key) throws CacheException {
			CacheObject co = cl.get(regionName, key);
			if(co != null){
				return co.getValue();
			}
			logger.debug("value for key {} is null",key);
			return null;
		}

		@Override
		public void put(Object key, Object value) throws CacheException {
			logger.debug("key : {} --> value : {}", key , value);
			cl.set(regionName, key, value);
		}

		@Override
		public void update(Object key, Object value) throws CacheException {
			put(key, value);
		}

		@Override
		public void remove(Object key) throws CacheException {
			cl.evict(regionName, key);
		}

		@Override
		public void clear() throws CacheException {
			cl.clear(regionName);
		}

		@Override
		public void destroy() throws CacheException {
			cl.clear(regionName);
			cl = null;
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

		@Override
		public Map toMap() {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
