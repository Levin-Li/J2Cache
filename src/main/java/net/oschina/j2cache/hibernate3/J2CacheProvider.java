package net.oschina.j2cache.hibernate3;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import net.sf.ehcache.Element;
import net.sf.ehcache.concurrent.CacheLockProvider;
import net.sf.ehcache.concurrent.LockType;
import net.sf.ehcache.util.Timestamper;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class J2CacheProvider implements CacheProvider {
	private static final Logger logger = LoggerFactory
			.getLogger(J2CacheProvider.class);

	private CacheChannel cl = null;

	@Override
	public Cache buildCache(String regionName, Properties properties)
			throws CacheException {
		if (cl == null) {
			logger.warn("J2CacheProvider is not start , you should call the start method first");
			return null;
		}
		return new J2Cache(cl, regionName);
	}

	@Override
	public long nextTimestamp() {
		return Timestamper.next();
	}

	@Override
	public void start(Properties properties) throws CacheException {
		if (cl != null) {
			logger.warn("you are trying to restart the J2CacheProvider , but it will be ok , because of J2CacheProvider is "
					+ "Singleton");
		}
		cl = CacheChannel.getInstance();
	}

	@Override
	public void stop() {
		cl.close();
	}

	@Override
	public boolean isMinimalPutsEnabledByDefault() {
		return true;
	}

	private static class J2Cache implements Cache {
		private static Logger logger = LoggerFactory.getLogger(J2Cache.class);

		private static final int SIXTY_THOUSAND_MS = 60000;

		private CacheChannel cl = null;
		private String regionName = null;
		private final CacheLockProvider lockProvider;

		public J2Cache(CacheChannel cl, String regionName) {
			this.cl = cl;
			this.regionName = regionName;
			Object context = cl.getInternalContext(regionName);
			if (context instanceof CacheLockProvider) {
				lockProvider = (CacheLockProvider) context;
			} else {
				lockProvider = null;
			}
		}

		@Override
		public Object read(Object key) throws CacheException {
			return get(key);
		}

		@Override
		public Object get(Object key) throws CacheException {
			CacheObject co = cl.get(regionName, key);
			if (co != null) {
				return co.getValue();
			}
			logger.debug("value for key {} is null", key);
			return null;
		}

		@Override
		public void put(Object key, Object value) throws CacheException {
			logger.debug("key : {} --> value : {}", key, value);
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
			cl.destroy(regionName);
		}

		@Override
		public void lock(Object key) throws CacheException {
			if (lockProvider != null) {
				lockProvider.getSyncForKey(key).lock(LockType.WRITE);
			}
		}

		@Override
		public void unlock(Object key) throws CacheException {
			if (lockProvider != null) {
				lockProvider.getSyncForKey(key).unlock(LockType.WRITE);
			}
		}

		@Override
		public long nextTimestamp() {
			return Timestamper.next();
		}

		@Override
		public int getTimeout() {
			return Timestamper.ONE_MS * SIXTY_THOUSAND_MS;
		}

		@Override
		public String getRegionName() {
			return regionName;
		}

		/**
		 * this method will take a long time. so I just return -1 , means this
		 * is not ture
		 */
		@Override
		public long getSizeInMemory() {
			return cl.getSizeInMemory(regionName);
		}

		@Override
		public long getElementCountInMemory() {
			return cl.getElementCountInMemory(regionName);
		}

		@Override
		public long getElementCountOnDisk() {
			return cl.getElementCountOnDisk(regionName);
		}

		@Override
		public Map toMap() {
			try {
				Map result = new HashMap();
				for (Object key : cl.keys(regionName)) {
					CacheObject e = cl.get(regionName, key);
					if (e != null) {
						result.put(key, e.getValue());
					}
				}
				return result;
			} catch (Exception e) {
				throw new CacheException(e);
			}
		}

		/**
		 * ture if lockProvider is not null
		 * 
		 * @return
		 */
		public final boolean canLockEntries() {
			return lockProvider != null;
		}

	}

}
