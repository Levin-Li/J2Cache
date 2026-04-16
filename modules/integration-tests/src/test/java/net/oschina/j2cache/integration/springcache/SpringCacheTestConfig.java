package net.oschina.j2cache.integration.springcache;

import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;
import net.oschina.j2cache.springcache.J2CacheSpringCacheManageAdapter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
class SpringCacheTestConfig {

    @Bean
    J2CacheBuilder j2CacheBuilder() throws Exception {
        J2CacheConfig config = J2CacheConfig.initFromConfig("/springcache/j2cache.properties");
        return J2CacheBuilder.init(config);
    }

    @Bean
    CacheManager cacheManager(J2CacheBuilder builder) {
        return new J2CacheSpringCacheManageAdapter(builder, true);
    }

    @Bean
    SpringCacheTestService springCacheTestService() {
        return new SpringCacheTestService();
    }
}
