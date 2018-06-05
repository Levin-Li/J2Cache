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
package net.oschina.j2cache.hibernate5.regions;

import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.GeneralDataRegion;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.jboss.logging.Logger;

import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.hibernate5.CacheRegion;
import net.oschina.j2cache.hibernate5.log.J2CacheMessageLogger;
import net.oschina.j2cache.hibernate5.strategy.J2CacheAccessStrategyFactory;

public class J2CacheGeneralDataRegion extends J2CacheDataRegion implements GeneralDataRegion {

    private static final J2CacheMessageLogger LOG = Logger.getMessageLogger(J2CacheMessageLogger.class,
            J2CacheGeneralDataRegion.class.getName());

    public J2CacheGeneralDataRegion(J2CacheAccessStrategyFactory accessStrategyFactory, CacheRegion underlyingCache,
            Properties properties) {
        super(accessStrategyFactory, underlyingCache, properties);
    }

    @Override
    public void evict(Object key) throws CacheException {
        try {
            getCache().evict(key);
        } catch (ClassCastException e) {
            throw new CacheException(e);
        } catch (IllegalStateException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void evictAll() throws CacheException {
        try {
            getCache().clear();
        } catch (IllegalStateException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Object get(SharedSessionContractImplementor session, Object key) throws CacheException {
        LOG.debugf("key: %s", key);
        if (key == null) {
            return null;
        } else {
            CacheObject value = getCache().get(key);
            if (value == null) {
                LOG.debugf("value for key %s is null", key);
                return null;
            } else {
                return value.getValue();
            }
        }
    }

    @Override
    public void put(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        LOG.debugf("key: %s value: %s", key, value);
        try {
            getCache().put(key, value);
        } catch (IllegalArgumentException e) {
            throw new CacheException(e);
        } catch (IllegalStateException e) {
            throw new CacheException(e);
        }
    }

}