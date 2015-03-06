package net.oschina.j2cache.hibernate.strategy;

import net.oschina.j2cache.hibernate.regions.J2CacheCollectionRegion;

public class ReadOnlyJ2CacheCollectionRegionAccessStrategy extends AbstractJ2CacheRegionAccessStrategy {

    public ReadOnlyJ2CacheCollectionRegionAccessStrategy(J2CacheCollectionRegion collectionMemcachedRegion){
        super(collectionMemcachedRegion);
    }
}
