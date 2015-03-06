package net.oschina.j2cache.hibernate.strategy;

import net.oschina.j2cache.hibernate.regions.J2CacheNaturalIdRegion;

import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

/**
 * @see org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy
 */
public abstract class AbstractJ2CacheNaturalIdRegionAccessStrategy extends J2CacheRegionAccessStrategy implements NaturalIdRegionAccessStrategy {

    private J2CacheNaturalIdRegion naturalIdRegion;

    public AbstractJ2CacheNaturalIdRegionAccessStrategy(J2CacheNaturalIdRegion naturalIdRegion){
        super(naturalIdRegion);
        this.naturalIdRegion = naturalIdRegion;
    }

    @Override
    public NaturalIdRegion getRegion() {
        return naturalIdRegion;
    }
}
