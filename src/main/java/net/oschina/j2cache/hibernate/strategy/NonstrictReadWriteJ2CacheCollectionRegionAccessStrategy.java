package net.oschina.j2cache.hibernate.strategy;

import net.oschina.j2cache.hibernate.regions.J2CacheCollectionRegion;

public class NonstrictReadWriteJ2CacheCollectionRegionAccessStrategy extends AbstractJ2CacheRegionAccessStrategy {

    public NonstrictReadWriteJ2CacheCollectionRegionAccessStrategy(J2CacheCollectionRegion collectionRegion){
        super(collectionRegion);
    }
}
