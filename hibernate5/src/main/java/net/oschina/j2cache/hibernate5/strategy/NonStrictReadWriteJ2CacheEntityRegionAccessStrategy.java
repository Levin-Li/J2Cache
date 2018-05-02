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
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import net.oschina.j2cache.hibernate5.regions.J2CacheEntityRegion;

public class NonStrictReadWriteJ2CacheEntityRegionAccessStrategy extends AbstractJ2CacheAccessStrategy<J2CacheEntityRegion> implements EntityRegionAccessStrategy {

    public NonStrictReadWriteJ2CacheEntityRegionAccessStrategy(J2CacheEntityRegion region, SessionFactoryOptions settings) {
        super(region, settings);
    }

    @Override
    public EntityRegion getRegion() {
        return super.region();
    }

    @Override
    public Object get(SharedSessionContractImplementor session, Object key, long txTimestamp) throws CacheException {
        return region().get(key);
    }

    @Override
    public boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
        if (minimalPutOverride && region().contains(key)) {
            return false;
        } else {
            region().put(key, value);
            return true;
        }
    }

    @Override
    public SoftLock lockItem(SharedSessionContractImplementor session, Object key, Object version) throws CacheException {
        return null;
    }

    @Override
    public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
        region().remove(key);
    }

    @Override
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
        return false;
    }

    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
        return false;
    }

    @Override
    public boolean update(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
        remove(session, key);
        return false;
    }

    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException {
        unlockItem(session, key, lock);
        return false;
    }

    @Override
    public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
        region().remove(key);
    }
}
