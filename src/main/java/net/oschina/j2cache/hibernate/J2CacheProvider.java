package net.oschina.j2cache.hibernate;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheExpiredListener;
import net.oschina.j2cache.J2Cache;
import net.oschina.j2cache.NullCacheProvider;
import net.oschina.j2cache.ehcache.EhCacheProvider;
import net.oschina.j2cache.redis.RedisCacheProvider;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class J2CacheProvider implements CacheProvider {
	
	private final static Logger log = LoggerFactory.getLogger(J2CacheProvider.class);
	private final static String CONFIG_FILE = "/j2cache.properties";
	
	private static net.oschina.j2cache.CacheProvider l1_provider;
	private static net.oschina.j2cache.CacheProvider l2_provider;
	
	private static CacheExpiredListener listener;
	
	private static String serializer ;
	
	@Override
	public long nextTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void start(Properties properties) throws CacheException {
		InputStream configStream = J2CacheProvider.class.getClassLoader().getParent().getResourceAsStream(CONFIG_FILE);
		if(configStream == null)
			configStream = J2CacheProvider.class.getResourceAsStream(CONFIG_FILE);
		if(configStream == null)
			throw new CacheException("Cannot find " + CONFIG_FILE + " !!!");
		
		Properties props = new Properties();
		
		J2CacheProvider.listener = CacheChannel.getInstance();
		try{
			props.load(configStream);
			configStream.close();
			
			J2CacheProvider.l1_provider = getProviderInstance(props.getProperty("cache.L1.provider_class"));
			J2CacheProvider.l1_provider.start(getProviderProperties(props, J2CacheProvider.l1_provider));
			log.info("Using L1 CacheProvider : " + l1_provider.getClass().getName());
			
			J2CacheProvider.l2_provider = getProviderInstance(props.getProperty("cache.L2.provider_class"));
			J2CacheProvider.l2_provider.start(getProviderProperties(props, J2CacheProvider.l2_provider));
			log.info("Using L2 CacheProvider : " + l2_provider.getClass().getName());
			
			J2CacheProvider.serializer = props.getProperty("cache.serialization");
			
		}catch(Exception e){
			throw new CacheException("Unabled to initialize cache providers", e);
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isMinimalPutsEnabledByDefault() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cache buildCache(String regionName, Properties properties)
			throws CacheException {
		return new J2Cache();	//TODO:参数
	}
	
	private final static net.oschina.j2cache.CacheProvider getProviderInstance(String value) throws Exception {
		if("ehcache".equalsIgnoreCase(value))
			return new EhCacheProvider();
		if("redis".equalsIgnoreCase(value))
			return new RedisCacheProvider();
		if("none".equalsIgnoreCase(value))
			return new NullCacheProvider();
		return (net.oschina.j2cache.CacheProvider)Class.forName(value).newInstance();
	}
	
	private final static Properties getProviderProperties(Properties props, net.oschina.j2cache.CacheProvider provider) {
		Properties new_props = new Properties();
		Enumeration<Object> keys = props.keys();
		String prefix = provider.name() + '.';
		while(keys.hasMoreElements()){
			String key = (String)keys.nextElement();
			if(key.startsWith(prefix))
				new_props.setProperty(key.substring(prefix.length()), props.getProperty(key));
		}
		return new_props;
	}
	
}
