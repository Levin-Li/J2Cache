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
package net.oschina.j2cache.hibernate5;

import java.util.Properties;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.access.AccessType;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.hibernate5.regions.J2CacheCollectionRegion;
import net.oschina.j2cache.hibernate5.regions.J2CacheEntityRegion;
import net.oschina.j2cache.hibernate5.regions.J2CacheNaturalIdRegion;
import net.oschina.j2cache.hibernate5.regions.J2CacheQueryResultsRegion;
import net.oschina.j2cache.hibernate5.regions.J2CacheTimestampsRegion;
import net.oschina.j2cache.hibernate5.strategy.J2CacheAccessStrategyFactory;
import net.oschina.j2cache.hibernate5.strategy.J2CacheAccessStrategyFactoryImpl;
import net.oschina.j2cache.hibernate5.strategy.NonstopAccessStrategyFactory;
import net.oschina.j2cache.hibernate5.util.Timestamper;

abstract class AbstractJ2CacheRegionFactory implements RegionFactory {

    /**
     * 
     */
    private static final long serialVersionUID = -6910876340668669622L;

    protected SessionFactoryOptions settings;

    protected CacheChannel channel;

    protected final J2CacheAccessStrategyFactory accessStrategyFactory = new NonstopAccessStrategyFactory(new J2CacheAccessStrategyFactoryImpl());

    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return true;
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new J2CacheEntityRegion(accessStrategyFactory, getCache(regionName), settings, metadata, properties);
    }

    @Override
    public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new J2CacheNaturalIdRegion(accessStrategyFactory, getCache(regionName), settings, metadata, properties);
    }

    @Override
    public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new J2CacheCollectionRegion(accessStrategyFactory, getCache(regionName), settings, metadata, properties);
    }

    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
        return new J2CacheQueryResultsRegion(accessStrategyFactory, getCache(regionName), properties);
    }

    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
        return new J2CacheTimestampsRegion(accessStrategyFactory, getCache(regionName), properties);
    }

    private CacheRegion getCache(String name) throws CacheException {
        return new J2CacheCacheRegion(channel,name);
    }

    public AccessType getDefaultAccessType() {
        return AccessType.READ_WRITE;
    }

    public void setChannel(CacheChannel channel) {
        this.channel = channel;
    }
}

