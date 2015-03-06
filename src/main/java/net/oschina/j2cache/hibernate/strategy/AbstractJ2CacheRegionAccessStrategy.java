package net.oschina.j2cache.hibernate.strategy;

import net.oschina.j2cache.hibernate.regions.J2CacheCollectionRegion;

import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;

public abstract class AbstractJ2CacheRegionAccessStrategy extends J2CacheRegionAccessStrategy implements CollectionRegionAccessStrategy {

    private J2CacheCollectionRegion collectionRegion;

    public AbstractJ2CacheRegionAccessStrategy(J2CacheCollectionRegion collectionRegion){
        super(collectionRegion);
        this.collectionRegion = collectionRegion;
    }

    @Override
    public CollectionRegion getRegion() {
        return collectionRegion;
    }
}
