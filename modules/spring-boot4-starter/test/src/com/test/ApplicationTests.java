package com.test;

import com.test.bean.TestBean;
import com.test.service.TestService;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.autoconfigure.J2CacheAutoConfiguration;
import net.oschina.j2cache.autoconfigure.J2CacheSpringCacheAutoConfiguration;
import net.oschina.j2cache.autoconfigure.J2CacheSpringRedisAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        classes = {
                TestService.class,
                J2CacheAutoConfiguration.class,
                J2CacheSpringCacheAutoConfiguration.class,
                J2CacheSpringRedisAutoConfiguration.class
        },
        properties = {
                "j2cache.config-location=classpath:/com/test/j2cache-test.properties",
                "spring.cache.type=GENERIC",
                "j2cache.open-spring-cache=true",
                "j2cache.j2CacheConfig.serialization=json",
                "j2cache.redis-client=jedis",
                "j2cache.cache-clean-mode=active",
                "j2cache.allow-null-values=true",
                "j2cache.l2-cache-open=true"
        }
)
class ApplicationTests {

    @Autowired
    private TestService testService;

    @Autowired
    private CacheChannel cacheChannel;

    @Test
    void testCache() {
        testService.reset();
        testService.evict();
        testService.getNum();
        testService.getNum();
        testService.getNum();
        testService.getNum();
        Integer value = testService.getNum();
        assertEquals(1, value);
    }

    @Test
    void clearCache() {
        testService.reset();
        testService.getNum();
        testService.reset();
        testService.evict();
        Integer value = testService.getNum();
        assertEquals(1, value);
    }

    @Test
    void beanCache() {
        testService.reset();
        testService.evict();
        testService.testBean();
        TestBean bean = testService.testBean();
        assertEquals(1, bean.getNum());
    }

    @Test
    void cacheChannelRoundTrip() {
        cacheChannel.set("test", "123", "321");
        CacheObject cacheObject = cacheChannel.get("test", "123");
        assertEquals("321", cacheObject.getValue());
    }
}
