package net.oschina.j2cache.hibernate7;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.cache.spi.support.RegionFactoryTemplate;
import org.hibernate.cache.spi.support.StorageAccess;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import java.util.Map;

public class J2CacheRegionFactory extends RegionFactoryTemplate {

    private CacheChannel cacheChannel;

    @Override
    protected void prepareForUse(SessionFactoryOptions settings, Map<String, Object> configValues) {
        if (cacheChannel == null) {
            cacheChannel = J2Cache.getChannel();
        }
    }

    @Override
    protected void releaseFromUse() {
        if (cacheChannel != null) {
            cacheChannel.close();
        }
    }

    @Override
    protected DomainDataStorageAccess createDomainDataStorageAccess(DomainDataRegionConfig regionConfig, DomainDataRegionBuildingContext buildingContext) {
        return new J2CacheStorageAccess(cacheChannel, qualify(regionConfig.getRegionName()));
    }

    @Override
    protected StorageAccess createQueryResultsRegionStorageAccess(String regionName, SessionFactoryImplementor sessionFactory) {
        return new J2CacheStorageAccess(cacheChannel, qualify(regionName));
    }

    @Override
    protected StorageAccess createTimestampsRegionStorageAccess(String regionName, SessionFactoryImplementor sessionFactory) {
        return new J2CacheStorageAccess(cacheChannel, qualify(regionName));
    }
}
