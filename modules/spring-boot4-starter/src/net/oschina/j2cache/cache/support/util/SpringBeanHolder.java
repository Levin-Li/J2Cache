package net.oschina.j2cache.cache.support.util;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
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
@Setter
@Accessors(chain = true)
@Slf4j
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

    @PostConstruct
    public void init() {
        log.debug("SpringBeanHolder init");
    }

    private SpringBeanHolder() {
    }

    public static SpringBeanHolder getInstance() {
        return instance;
    }

    public <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

}
