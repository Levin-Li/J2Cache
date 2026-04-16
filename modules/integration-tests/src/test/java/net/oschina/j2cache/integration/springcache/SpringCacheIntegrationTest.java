package net.oschina.j2cache.integration.springcache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(SpringCacheTestConfig.class)
class SpringCacheIntegrationTest {

    @Autowired
    private SpringCacheTestService service;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void resetState() {
        service.reset();
        cacheManager.getCache("numbers").clear();
    }

    @Test
    void cacheableCachesSecondInvocation() {
        int first = service.load("demo");
        int second = service.load("demo");

        assertEquals(first, second);
        assertEquals(1, first);
    }

    @Test
    void cacheEvictForcesReload() {
        int first = service.load("demo-evict");
        service.evict("demo-evict");
        int second = service.load("demo-evict");

        assertEquals(1, first);
        assertEquals(2, second);
    }
}
