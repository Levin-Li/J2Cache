package net.oschina.j2cache.redis;


import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;

import net.sf.ehcache.CacheException;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis 连接池
 * @author Winter Lau
 */
public class RedisPool extends JedisPool {
	
	private final static String CONFIG_FILE = "/redis.properties";
	private static JedisPoolConfig config = new JedisPoolConfig();
	private static JedisPool pool;

	private final static String host;
	private final static int port;
	private final static int timeout;
	private final static String password;
	private final static int database;
	
	static {
		Properties properties = new Properties();
		try {
			properties.load(RedisPool.class.getResourceAsStream(CONFIG_FILE));
			BeanUtils.populate(config, properties);

			host = properties.getProperty("host");
			port = Integer.valueOf(properties.getProperty("port", "6379"));
			timeout = Integer.valueOf(properties.getProperty("timeout","2000"));
			password = properties.getProperty("password");
			database = Integer.valueOf(properties.getProperty("database", "0"));
			
		} catch (Exception e) {
			throw new CacheException(
					"CahceException:RedisConfig init failed cause by:", e);
		}
	}

	private RedisPool(JedisPoolConfig config) {
		super(config, host, port, timeout, password, database);
	}

	public static JedisPool me() {
		if (null == pool) {
			synchronized (RedisPool.class) {
				if (null == pool) {
					pool = new RedisPool(config);
				}
			}
		}
		return pool;
	}

}
