package net.oschina.j2cache.hibernate.regions;

import net.oschina.j2cache.CacheChannel;

import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.TransactionalDataRegion;
import org.hibernate.cfg.Settings;

public class J2CacheTransactionalDataRegion extends J2CacheGeneralDataRegion implements TransactionalDataRegion {

    protected final CacheDataDescription metadata;

    public J2CacheTransactionalDataRegion(String name, CacheChannel cache, Settings settings,
                                          CacheDataDescription metadata){
        super(name, cache, settings);
        this.metadata = metadata;
    }

    @Override
    public boolean isTransactionAware() {
        return false;
    }

    @Override
    public CacheDataDescription getCacheDataDescription() {
        return this.metadata;
    }

}
