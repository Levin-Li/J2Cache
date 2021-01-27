package net.oschina.j2cache.cluster;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.J2Cache;
import org.junit.Test;

/**
 * @author lookang
 * @date 2020/12/17
 */
public class ClusterPolicyFactoryTest2 {

    @Test
    public void clear() {
        String region = "Users";
        String key = "key";
        CacheChannel channel = J2Cache.getChannel();
        CacheObject cacheObject = channel.get(region, key);
        System.out.println(cacheObject.getValue());
        channel.clear("Users");
        System.out.println(12123);
    }
}
