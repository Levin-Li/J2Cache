package net.oschina.j2cache.hibernate;

import junit.framework.Test;

import org.hibernate.cfg.Environment;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;
import org.hibernate.test.cache.BaseCacheProviderTestCase;

public class J2CacheProviderTest extends BaseCacheProviderTestCase {

    public J2CacheProviderTest(String x){
        super(x);
    }

    public static Test suite() {
        return new FunctionalTestClassTestSuite(J2CacheProviderTest.class);
    }

    @Override
    public String getCacheConcurrencyStrategy() {
        return "nonstrict-read-write";
    }

    @Override
    protected Class getCacheProvider() {
        return J2CacheProvider.class;
    }

    @Override
    protected String getConfigResourceKey() {
        return Environment.CACHE_PROVIDER_CONFIG;
    }

    @Override
    protected String getConfigResourceLocation() {
        return "hibernate.properties";
    }

    @Override
    protected boolean useTransactionManager() {
        return false;
    }

}
