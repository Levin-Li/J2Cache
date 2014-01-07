package net.oschina.j2cache.ehcache;

import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

import java.util.Properties;

/**
 * User: Michael Chen
 * Email: yidongnan@gmail.com
 * Date: 14-1-3
 * Time: 上午11:15
 */
public class EhCacheEventListenerFactory extends CacheEventListenerFactory {
    @Override
    public CacheEventListener createCacheEventListener(Properties properties) {
        return new EhCacheEventListener();
    }
}
