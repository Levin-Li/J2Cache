package net.oschina.j2cache.cache.support.util;

import com.ctrip.framework.apollo.spring.config.ConfigPropertySource;
import net.oschina.j2cache.J2CacheConfig;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

public class SpringJ2CacheConfigUtil {

	/**
	 * 从spring环境变量中查找j2cache配置
	 */
	public static final J2CacheConfig initFromConfig(StandardEnvironment environment) {
		J2CacheConfig config = new J2CacheConfig();
		config.setSerialization(environment.getProperty("j2cache.serialization"));
		config.setBroadcast(environment.getProperty("j2cache.broadcast"));
		config.setL1CacheName(environment.getProperty("j2cache.L1.provider_class"));
		config.setL2CacheName(environment.getProperty("j2cache.L2.provider_class"));
		config.setSyncTtlToRedis(!"false".equalsIgnoreCase(environment.getProperty("j2cache.sync_ttl_to_redis")));
		config.setDefaultCacheNullObject(
				"true".equalsIgnoreCase(environment.getProperty("j2cache.default_cache_null_object")));
		String l2_config_section = environment.getProperty("j2cache.L2.config_section");
		if (l2_config_section == null || l2_config_section.trim().equals(""))
			l2_config_section = config.getL2CacheName();
		final String l2_section = l2_config_section;

		environment.getPropertySources().forEach(a -> loadConfig(a, environment, config, l2_section));

		return config;
	}

	public static void loadConfig(PropertySource a, Environment environment, J2CacheConfig config, String l2_section) {
		if (a instanceof CompositePropertySource) {

			((CompositePropertySource) a)
					.getPropertySources()
					.forEach(b -> loadConfig(b, environment, config, l2_section));
		} else if (a instanceof MapPropertySource) {
			MapPropertySource c = (MapPropertySource) a;
			c.getSource().forEach((k, v) -> {});

		} else if (a instanceof ConfigPropertySource) {
			for (String propertyName : ((ConfigPropertySource) a).getPropertyNames()) {
				loadConfigValue(environment, config, l2_section, propertyName);
			}
		}
	}

	public static void loadConfigValue(Environment environment, J2CacheConfig config, String l2_section, String key) {
		if (key.startsWith(config.getBroadcast() + ".")) {
			config
					.getBroadcastProperties()
					.setProperty(key.substring((config.getBroadcast() + ".").length()), environment.getProperty(key));
		}
		if (key.startsWith(config.getL1CacheName() + ".")) {
			config
					.getL1CacheProperties()
					.setProperty(key.substring((config.getL1CacheName() + ".").length()), environment.getProperty(key));
		}
		if (key.startsWith(l2_section + ".")) {
			config
					.getL2CacheProperties()
					.setProperty(key.substring((l2_section + ".").length()), environment.getProperty(key));
		}
	}
}
