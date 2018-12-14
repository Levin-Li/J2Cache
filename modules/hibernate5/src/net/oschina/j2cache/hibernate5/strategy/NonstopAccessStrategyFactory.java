/**
 * Copyright (c) 2015-2017.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache.hibernate5.strategy;

import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

import net.oschina.j2cache.hibernate5.nonstop.HibernateNonstopCacheExceptionHandler;
import net.oschina.j2cache.hibernate5.nonstop.NonstopAwareCollectionRegionAccessStrategy;
import net.oschina.j2cache.hibernate5.nonstop.NonstopAwareEntityRegionAccessStrategy;
import net.oschina.j2cache.hibernate5.nonstop.NonstopAwareNaturalIdRegionAccessStrategy;
import net.oschina.j2cache.hibernate5.regions.J2CacheCollectionRegion;
import net.oschina.j2cache.hibernate5.regions.J2CacheEntityRegion;
import net.oschina.j2cache.hibernate5.regions.J2CacheNaturalIdRegion;


public class NonstopAccessStrategyFactory implements J2CacheAccessStrategyFactory {

    private final J2CacheAccessStrategyFactory actualFactory;

    public NonstopAccessStrategyFactory(J2CacheAccessStrategyFactory actualFactory) {
        this.actualFactory = actualFactory;
    }

    @Override
    public EntityRegionAccessStrategy createEntityRegionAccessStrategy(J2CacheEntityRegion entityRegion, AccessType accessType) {
        return new NonstopAwareEntityRegionAccessStrategy(actualFactory.createEntityRegionAccessStrategy(entityRegion, accessType), HibernateNonstopCacheExceptionHandler.getInstance());
    }

    @Override
    public NaturalIdRegionAccessStrategy createNaturalIdRegionAccessStrategy(J2CacheNaturalIdRegion naturalIdRegion, AccessType accessType) {
        return new NonstopAwareNaturalIdRegionAccessStrategy(actualFactory.createNaturalIdRegionAccessStrategy(naturalIdRegion, accessType), HibernateNonstopCacheExceptionHandler.getInstance());
    }

    @Override
    public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(J2CacheCollectionRegion collectionRegion, AccessType accessType) {
        return new NonstopAwareCollectionRegionAccessStrategy(actualFactory.createCollectionRegionAccessStrategy(collectionRegion, accessType), HibernateNonstopCacheExceptionHandler.getInstance());
    }

}
