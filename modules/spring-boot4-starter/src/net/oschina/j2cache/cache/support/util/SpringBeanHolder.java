package net.oschina.j2cache.cache.support.util;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.oschina.j2cache.J2CacheConfig;
import net.oschina.j2cache.autoconfigure.J2CacheBootConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.io.Serializable;

/**
 * spring 工具类
 *
 * @author zhangsaizz
 */
@Getter
//@Accessors(chain = true)
public class SpringBeanHolder {

    public final static SpringBeanHolder instance = new SpringBeanHolder();

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    RedisMessageListenerContainer listenerContainer;

    @Autowired
    J2CacheBootConfig bootConfig;

    @Autowired
    J2CacheConfig config;

//    J2CacheConfig

    private SpringBeanHolder() {
    }

}
