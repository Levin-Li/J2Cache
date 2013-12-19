package net.oschina.j2cache;

import java.io.Serializable;

import net.oschina.j2cache.ehcache.EhCacheProvider;
import net.oschina.j2cache.redis.RedisCacheProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存管理器
 * @author liudong
 */
class CacheManager {

	private final static Logger log = LoggerFactory.getLogger(CacheManager.class);

	private static CacheProvider l1_provider;
	private static CacheProvider l2_provider;
	
	private static CacheExpiredListener listener;
	
	public static void initCacheProvider(CacheExpiredListener listener){
		CacheManager.listener = listener;
		try{
			CacheManager.l1_provider = new EhCacheProvider();
			CacheManager.l1_provider.start();
			log.info("Using L1 CacheProvider : " + l1_provider.getClass().getName());
			
			CacheManager.l2_provider = new RedisCacheProvider();
			CacheManager.l2_provider.start();
			log.info("Using L2 CacheProvider : " + l2_provider.getClass().getName());
		}catch(Exception e){
			throw new CacheException("Unabled to initialize cache providers", e);
		}
	}

	private final static Cache _GetCache(int level, String cache_name, boolean autoCreate) {
		return ((level==1)?l1_provider:l2_provider).buildCache(cache_name, autoCreate, listener);
	}

	/**
	 * 获取缓存中的数据
	 * @param level
	 * @param name
	 * @param key
	 * @return
	 */
	public final static Object get(int level, String name, Serializable key){
		//System.out.println("GET1 => " + name+":"+key);
		if(name!=null && key != null)
			return _GetCache(level, name, true).get(key);
		return null;
	}
	
	/**
	 * 获取缓存中的数据
	 * @param <T>
	 * @param level
	 * @param resultClass
	 * @param name
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final static <T> T get(int level, Class<T> resultClass, String name, Serializable key){
		//System.out.println("GET2 => " + name+":"+key);
		if(name!=null && key != null)
			return (T)_GetCache(level, name, true).get(key);
		return null;
	}
	
	/**
	 * 写入缓存
	 * @param level
	 * @param name
	 * @param key
	 * @param value
	 */
	public final static void set(int level, String name, Serializable key, Serializable value){
		//System.out.println("SET => " + name+":"+key+"="+value);
		if(name!=null && key != null && value!=null)
			_GetCache(level, name, true).put(key, value);		
	}
	
	/**
	 * 清除缓冲中的某个数据
	 * @param level
	 * @param name
	 * @param key
	 */
	public final static void evict(int level, String name, Serializable key){
		if(name!=null && key != null)
			_GetCache(level, name, true).remove(key);		
	}

	/**
	 * 清除缓冲中的某个数据
	 * @param level
	 * @param name
	 * @param key
	 */
	public final static void justEvict(int level, String name, Serializable key){
		if(name!=null && key != null){
			Cache cache = _GetCache(level, name, false);
			if(cache != null)
				cache.remove(key);
		}
	}

}
