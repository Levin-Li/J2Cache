package net.oschina.j2cache.redis;

import java.util.concurrent.ConcurrentHashMap;

import net.oschina.j2cache.Cache;
import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.CacheExpiredListener;
import net.oschina.j2cache.CacheProvider;
import redis.clients.jedis.BinaryJedis;

/**
 * Redis 缓存实现
 * @author Winter Lau
 */
public class RedisCacheProvider implements CacheProvider {

	private ConcurrentHashMap<String, RedisCache> _CacheManager ;
	
	@Override
	public Cache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener) throws CacheException {
		try {
			RedisCache cache = _CacheManager.get(regionName);
			if(cache == null){
				BinaryJedis jedis = RedisPool.me().getResource();
				cache = new RedisCache(regionName, jedis);
				_CacheManager.put(regionName, cache);
			}
			return cache;
		} catch (Exception e) {
			throw new CacheException("Unabled to build redis cache,name="+regionName, e);
		}
	}

	@Override
	public void start() throws CacheException {
	}

	@Override
	public void stop() {
		RedisPool.me().destroy();
	}

}
