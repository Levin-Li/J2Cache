package net.oschina.j2cache.autoconfigure;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;
import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;
import net.oschina.j2cache.cache.support.util.SpringJ2CacheConfigUtil;
import net.oschina.j2cache.cache.support.util.SpringBeanHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.StandardEnvironment;

/**
 * 启动入口
 *
 * @author zhangsaizz
 */
@ConditionalOnClass(J2Cache.class)
@EnableConfigurationProperties({J2CacheBootConfig.class})
@Configuration
@PropertySource(value = "${j2cache.config-location}", encoding = "UTF-8", ignoreResourceNotFound = true)
public class J2CacheAutoConfiguration {

    @Autowired
    private StandardEnvironment standardEnvironment;

    @Bean
    public net.oschina.j2cache.J2CacheConfig j2CacheConfig() {
        net.oschina.j2cache.J2CacheConfig cacheConfig = SpringJ2CacheConfigUtil.initFromConfig(standardEnvironment);
        return cacheConfig;
    }

    @Bean
    public SpringBeanHolder springBeanHolder(@Autowired J2CacheConfig j2CacheConfig) {
        return SpringBeanHolder.instance.setConfig(j2CacheConfig);
    }

    @Bean
    public CacheChannel cacheChannel(@Autowired net.oschina.j2cache.J2CacheConfig j2CacheConfig, @Autowired SpringBeanHolder springBeanHolder) {
        J2CacheBuilder builder = J2CacheBuilder.init(j2CacheConfig);
        return builder.getChannel();
    }

}
