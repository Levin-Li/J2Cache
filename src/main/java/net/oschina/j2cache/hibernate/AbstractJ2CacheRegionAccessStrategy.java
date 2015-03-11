package net.oschina.j2cache.hibernate;

import org.hibernate.cache.TransactionalDataRegion;

public class AbstractJ2CacheRegionAccessStrategy<T extends TransactionalDataRegion> {

	protected T region;
	
	
	public AbstractJ2CacheRegionAccessStrategy(T region) {
		this.region = region;
	}

	
}
