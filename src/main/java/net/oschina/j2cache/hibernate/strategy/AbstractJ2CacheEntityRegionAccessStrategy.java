package net.oschina.j2cache.hibernate.strategy;

import net.oschina.j2cache.hibernate.regions.J2CacheEntityRegion;

import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;

/**
 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy
 */
public abstract class AbstractJ2CacheEntityRegionAccessStrategy extends J2CacheRegionAccessStrategy implements EntityRegionAccessStrategy {

    private J2CacheEntityRegion entityRegion;

    public AbstractJ2CacheEntityRegionAccessStrategy(J2CacheEntityRegion entityRegion){
        super(entityRegion);
        this.entityRegion = entityRegion;
    }

    @Override
    public EntityRegion getRegion() {
        return entityRegion;
    }
}
