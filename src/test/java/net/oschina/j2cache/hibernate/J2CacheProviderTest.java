package net.oschina.j2cache.hibernate;

import java.util.Map;

import junit.framework.Test;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cache.ReadWriteCache;
import org.hibernate.cfg.Environment;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.hibernate.test.cache.BaseCacheProviderTestCase;
import org.hibernate.test.cache.Item;
import org.hibernate.test.cache.VersionedItem;

public class J2CacheProviderTest extends BaseCacheProviderTestCase {

    public J2CacheProviderTest(String x){
        super(x);
    }

    public static Test suite() {
        return new FunctionalTestClassTestSuite(J2CacheProviderTest.class);
    }

    @Override
    public String getCacheConcurrencyStrategy() {
        return "read-write";
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

    @Override
    public void testQueryCacheInvalidation() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Item i = new Item();
        i.setName("widget");
        i.setDescription("A really top-quality, full-featured widget.");
        s.persist(i);
        t.commit();
        s.close();

        SecondLevelCacheStatistics slcs = s.getSessionFactory()
            .getStatistics()
            .getSecondLevelCacheStatistics(Item.class.getName());

        assertEquals(slcs.getPutCount(), 1);
        assertEquals(slcs.getEntries().size(), 1);

        s = openSession();
        t = s.beginTransaction();
        i = (Item) s.get(Item.class, i.getId());

        assertEquals(slcs.getHitCount(), 1);
        assertEquals(slcs.getMissCount(), 0);

        i.setDescription("A bog standard item");

        t.commit();
        s.close();

        assertEquals(slcs.getPutCount(), 2);

        Object entry = slcs.getEntries().get(i.getId());
        Map map;
        if (entry instanceof ReadWriteCache.Item) {
            map = (Map) ((ReadWriteCache.Item) entry).getValue();
        } else {
            map = (Map) entry;
        }
        assertTrue(map.get("description").equals("A bog standard item"));
        assertTrue(map.get("name").equals("widget"));
    }

    @Override
    public void testEmptySecondLevelCacheEntry() throws Exception {
        getSessions().evictEntity(Item.class.getName());
        Statistics stats = getSessions().getStatistics();
        stats.clear();
        SecondLevelCacheStatistics statistics = stats.getSecondLevelCacheStatistics(Item.class.getName());
        Map cacheEntries = statistics.getEntries();
        assertEquals(0, cacheEntries.size());
    }

    @Override
    public void testStaleWritesLeaveCacheConsistent() {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        VersionedItem item = new VersionedItem();
        item.setName("steve");
        item.setDescription("steve's item");
        s.save(item);
        txn.commit();
        s.close();

        Long initialVersion = item.getVersion();

        // manually revert the version property
        item.setVersion(new Long(item.getVersion().longValue() - 1));

        try {
            s = openSession();
            txn = s.beginTransaction();
            s.update(item);
            txn.commit();
            s.close();
            fail("expected stale write to fail");
        } catch (Throwable expected) {
            // expected behavior here
            if (txn != null) {
                try {
                    txn.rollback();
                } catch (Throwable ignore) {
                }
            }
        } finally {
            if ((s != null) && s.isOpen()) {
                try {
                    s.close();
                } catch (Throwable ignore) {
                }
            }
        }

        // check the version value in the cache...
        SecondLevelCacheStatistics slcs = sfi().getStatistics()
            .getSecondLevelCacheStatistics(VersionedItem.class.getName());

        Object entry = slcs.getEntries().get(item.getId());
        Long cachedVersionValue;
        if (entry instanceof ReadWriteCache.Lock) {
            // FIXME don't know what to test here
            cachedVersionValue = new Long(((ReadWriteCache.Lock) entry).getUnlockTimestamp());
        } else {
            cachedVersionValue = (Long) ((Map) entry).get("_version");
            assertEquals(initialVersion.longValue(), cachedVersionValue.longValue());
        }

        // cleanup
        s = openSession();
        txn = s.beginTransaction();
        item = (VersionedItem) s.load(VersionedItem.class, item.getId());
        s.delete(item);
        txn.commit();
        s.close();
    }

    @Override
    protected void cleanupTest() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Item item = (Item) s.get(Item.class, 1L);
        if (item != null) {
            s.delete(item);
        }
        t.commit();
        s.close();

        s = openSession();
        t = s.beginTransaction();
        VersionedItem versionedItem = (VersionedItem) s.get(VersionedItem.class, 1L);
        if (versionedItem != null) {
            s.delete(versionedItem);
        }
        t.commit();
        s.close();
    }

}
