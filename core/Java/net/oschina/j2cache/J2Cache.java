package net.oschina.j2cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 缓存入口
 *
 * @author winterlau
 */
public class J2Cache {

    private final static Logger log = LoggerFactory.getLogger(J2Cache.class);

    private final static String CONFIG_FILE = "/j2cache.properties";
    private static CacheChannel channel;
    private static Properties config;
    private static AtomicBoolean initlized = new AtomicBoolean(false);


    public static void init() {
        init(CONFIG_FILE);
    }

    public static void init(String filePath) {
        if (initlized.compareAndSet(false, true)) {
            try {
                config = loadConfig(filePath);
                String cache_broadcast = config.getProperty("cache.broadcast");
                if ("redis".equalsIgnoreCase(cache_broadcast)) {
                    channel = RedisCacheChannel.getInstance();
                } else if ("jgroups".equalsIgnoreCase(cache_broadcast)) {
                    channel = JGroupsCacheChannel.getInstance();
                } else {
                    initlized.set(false);
                    throw new CacheException("Cache Channel not defined. name = " + cache_broadcast);
                }
            } catch (IOException e) {
                initlized.set(false);
                throw new CacheException("Unabled to load j2cache configuration " + filePath, e);
            }
        } else {
            log.warn("J2cache initlized alerday!");
        }
    }

    private static void checkInitlized() {
        if (!initlized.get()) {
            throw new CacheException("J2cache init not yet! You should call j2Cache.init(String) first!");
        }
    }

    public static CacheChannel getChannel() {
        checkInitlized();
        return channel;
    }

    public static Properties getConfig() {
        checkInitlized();
        return config;
    }

    /**
     * 加载配置
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    private static Properties loadConfig(String filePath) throws IOException {
        if (filePath == null || filePath.trim().length() == 0) filePath = CONFIG_FILE;
        log.info("Load J2Cache Config File : [{}].", filePath);
        InputStream configStream = J2Cache.class.getResourceAsStream(filePath);
        if (configStream == null)
            configStream = CacheManager.class.getResourceAsStream(filePath);
        if (configStream == null)
            throw new CacheException("Cannot find " + filePath + " !!!");

        Properties props = new Properties();

        try {
            props.load(configStream);
        } finally {
            configStream.close();
        }

        return props;
    }

}
