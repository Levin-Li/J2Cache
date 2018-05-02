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

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;

import net.oschina.j2cache.hibernate5.regions.J2CacheTransactionalDataRegion;


abstract class AbstractJ2CacheAccessStrategy<T extends J2CacheTransactionalDataRegion> {

    private final T region;
    private final SessionFactoryOptions settings;

    AbstractJ2CacheAccessStrategy(T region, SessionFactoryOptions settings) {
        this.region = region;
        this.settings = settings;
    }

    protected T region() {
        return region;
    }

    protected SessionFactoryOptions settings() {
        return settings;
    }

    public final boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version) throws CacheException {
        return putFromLoad(session, key, value, txTimestamp, version, settings.isMinimalPutsEnabled() );
    }

    public abstract boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
            throws CacheException;

    public final SoftLock lockRegion() {
        return null;
    }

    public final void unlockRegion(SoftLock lock) throws CacheException {
        region.clear();
    }

    public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
    }

    public final void removeAll() throws CacheException {
        region.clear();
    }

    public final void evict(Object key) throws CacheException {
        region.remove( key );
    }

    public final void evictAll() throws CacheException {
        region.clear();
    }
    

    public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
        return DefaultCacheKeysFactory.staticCreateEntityKey( id, persister, factory, tenantIdentifier );
    }

    public Object generateCacheKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
        return DefaultCacheKeysFactory.staticCreateCollectionKey( id, persister, factory, tenantIdentifier );
    }

    public Object getCacheKeyId(Object cacheKey) {
        return DefaultCacheKeysFactory.staticGetEntityId(cacheKey);
    }

}
