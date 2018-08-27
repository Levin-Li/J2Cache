package net.oschina.j2cache.autoconfigure;

import net.oschina.j2cache.cache.support.util.J2CacheSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

/**
 * 对spring redis支持的配置入口
 * 
 * @author zhangsaizz
 *
 */
@Configuration
@AutoConfigureAfter({ RedisAutoConfiguration.class })
@AutoConfigureBefore({ J2CacheAutoConfiguration.class })
public class J2CacheSpringRedisAutoConfiguration {


	private static final Logger log = LoggerFactory.getLogger(J2CacheSpringRedisAutoConfiguration.class);

	@Bean("j2CacheRedisTemplate")
	public RedisTemplate<String, Serializable> j2CacheRedisTemplate(
			RedisConnectionFactory j2CahceRedisConnectionFactory, @Qualifier("j2CacheValueSerializer") RedisSerializer<Object> j2CacheSerializer) {
		RedisTemplate<String, Serializable> template = new RedisTemplate<String, Serializable>();
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setDefaultSerializer(j2CacheSerializer);
		template.setConnectionFactory(j2CahceRedisConnectionFactory);
		return template;
	}

	@Bean("j2CacheValueSerializer")
	@ConditionalOnMissingBean(name = "j2CacheValueSerializer")
	public RedisSerializer<Object> j2CacheValueSerializer() {
		return new J2CacheSerializer();
	}

	@Bean("j2CacheRedisMessageListenerContainer")
	RedisMessageListenerContainer container(RedisConnectionFactory j2CahceRedisConnectionFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(j2CahceRedisConnectionFactory);
		return container;
	}

}
