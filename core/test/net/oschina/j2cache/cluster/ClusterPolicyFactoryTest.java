package net.oschina.j2cache.cluster;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;
import org.junit.Test;

public class ClusterPolicyFactoryTest {

    @Test
    public void clear() {
        String region = "Users";
        String key = "key";
        CacheChannel channel = J2Cache.getChannel();
        channel.set(region, key, "key2");
        System.out.println(12123);
        channel.clear("Users");
//        channel.getL1Provider()
        System.out.println(12123);
    }
}