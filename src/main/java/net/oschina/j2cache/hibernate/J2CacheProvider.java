package net.oschina.j2cache.hibernate;

import java.util.Properties;

import net.oschina.j2cache.CacheChannel;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;
import org.hibernate.cache.Timestamper;

/**
 * Cache Provider plugin for Hibernate Use
 * <code>hibernate.cache.provider_class=net.oschina.j2cache.hibernate.J2CacheProvider in Hibernate 3.x</code>
 * 
 * @version <pre>
 * Author	Version		Date		Changes
 * liuye 	1.0  		2015年3月5日 	Created
 *
 * </pre>
 * @since 1.
 */
@SuppressWarnings("deprecation")
public class J2CacheProvider implements CacheProvider {

    private CacheChannel channel = CacheChannel.getInstance();

    /*
     * (non-Javadoc)
     * @see org.hibernate.cache.CacheProvider#buildCache(java.lang.String,
     * java.util.Properties)
     */
    @Override
    public Cache buildCache(String regionName, Properties properties) throws CacheException {
        return new J2Cache(regionName, channel);
    }

    /*
     * (non-Javadoc)
     * @see org.hibernate.cache.CacheProvider#nextTimestamp()
     */
    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    /*
     * (non-Javadoc)
     * @see org.hibernate.cache.CacheProvider#start(java.util.Properties)
     */
    @Override
    public void start(Properties properties) throws CacheException {
    }

    /*
     * (non-Javadoc)
     * @see org.hibernate.cache.CacheProvider#stop()
     */
    @Override
    public void stop() {
        //channel.close();
    }

    /*
     * (non-Javadoc)
     * @see org.hibernate.cache.CacheProvider#isMinimalPutsEnabledByDefault()
     */
    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return false;
    }
}
