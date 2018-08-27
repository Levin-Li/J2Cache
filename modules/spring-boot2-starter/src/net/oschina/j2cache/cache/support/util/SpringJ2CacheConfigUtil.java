package net.oschina.j2cache.cache.support.util;

import net.oschina.j2cache.J2CacheConfig;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.StandardEnvironment;

import java.util.Map;

public class SpringJ2CacheConfigUtil {

	/**
	 * 从spring环境变量中查找j2cache配置
	 */
	public final static J2CacheConfig initFromConfig(StandardEnvironment environment){
		J2CacheConfig config = new J2CacheConfig();

		config.setSerialization(environment.getProperty("j2cache.serialization"));
		config.setBroadcast(environment.getProperty("j2cache.broadcast"));
		config.setL1CacheName(environment.getProperty("j2cache.L1.provider_class"));
		config.setL2CacheName(environment.getProperty("j2cache.L2.provider_class"));
		config.setSyncTtlToRedis(!"false".equalsIgnoreCase(environment.getProperty("j2cache.sync_ttl_to_redis")));
		config.setDefaultCacheNullObject("true".equalsIgnoreCase(environment.getProperty("j2cache.default_cache_null_object")));
		String l2_config_section = environment.getProperty("j2cache.L2.config_section");
		if (l2_config_section == null || l2_config_section.trim().equals(""))
			l2_config_section = config.getL2CacheName();
		final String l2_section = l2_config_section;

		environment.getPropertySources().forEach(a -> {
			if(a instanceof OriginTrackedMapPropertySource) {
				Map<String, Object> c = ((OriginTrackedMapPropertySource) a).getSource();

				c.forEach((k,v) -> {
					String key = k;
					String value=((OriginTrackedValue) v).getValue().toString();
					String prefix="j2cache."+config.getBroadcast()+".";

					if (key.startsWith(prefix)) {
						config.getBroadcastProperties().setProperty(key.substring(prefix.length()),  value);
					}
					prefix="j2cache."+config.getL1CacheName()+".";
					if (key.startsWith(prefix)) {
						config.getL1CacheProperties().setProperty(key.substring(prefix.length()),  value);
					}

					prefix="j2cache."+l2_section+".";
					if (key.startsWith(prefix)) {
						config.getL2CacheProperties().setProperty(key.substring(prefix.length()), value);
					}
				});
			}
		});
		return config;
	}
}
