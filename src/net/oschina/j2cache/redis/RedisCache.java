package net.oschina.j2cache.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.SerializationUtils;

import net.oschina.j2cache.Cache;
import net.oschina.j2cache.CacheException;

import redis.clients.jedis.BinaryJedis;

/**
 * Redis 缓存实现
 * @author winterlau
 */
public class RedisCache implements Cache {

	private String region;
	private BinaryJedis cache;

	public RedisCache(String region, BinaryJedis jedis) {
		this.cache = jedis;
		this.region = region;
	}

	@Override
	public Object get(Object key) throws CacheException {
		try {
			if (null == key)
				return null;
			byte[] b = cache.get(String.valueOf(region + ":" + key).getBytes());
			return b == null ? null : SerializationUtils.deserialize(b);
		} catch (CacheException e) {
			return null;
		}
	}

	@Override
	public void put(Object key, Object value) throws CacheException {
		cache.set(
				String.valueOf(region + ":" + key).getBytes(),
				value == null ? null : SerializationUtils.serialize((Serializable) value));
	}

	@Override
	public void update(Object key, Object value) throws CacheException {
		put(key, value);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List keys() throws CacheException {
		try {
			List<Object> keys = new ArrayList<Object>();
			Set<byte[]> list = cache.keys(String.valueOf("*").getBytes());
			for (byte[] bs : list) {
				keys.add(bs == null ? null : SerializationUtils.deserialize(bs));
			}
			return keys;
		} catch (CacheException e) {
			return null;
		}
	}

	@Override
	public void remove(Object key) throws CacheException {
		cache.expire(String.valueOf(region + ":" + key).getBytes(), 0);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void clear() throws CacheException {
		List keys = this.keys();
		for (Object key : keys) {
			this.remove(key);
		}
	}

	@Override
	public void destroy() throws CacheException {
		this.clear();
	}

}
