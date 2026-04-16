package net.oschina.j2cache.integration.springcache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.concurrent.atomic.AtomicInteger;

class SpringCacheTestService {

    private final AtomicInteger counter = new AtomicInteger();

    void reset() {
        counter.set(0);
    }

    @Cacheable(cacheNames = "numbers", key = "#p0")
    int load(String key) {
        return counter.incrementAndGet();
    }

    @CacheEvict(cacheNames = "numbers", key = "#p0")
    void evict(String key) {
    }
}
