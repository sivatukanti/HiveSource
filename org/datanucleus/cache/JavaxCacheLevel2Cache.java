// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.cache;

import java.util.Iterator;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.identity.OID;
import org.datanucleus.metadata.IdentityType;
import java.util.Arrays;
import org.datanucleus.util.NucleusLogger;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import org.datanucleus.PersistenceConfiguration;
import javax.cache.CacheManager;
import javax.cache.spi.CachingProvider;
import javax.cache.CacheException;
import org.datanucleus.exceptions.NucleusException;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.Caching;
import org.datanucleus.NucleusContext;
import javax.cache.Cache;

public class JavaxCacheLevel2Cache extends AbstractLevel2Cache
{
    private final Cache cache;
    
    public JavaxCacheLevel2Cache(final NucleusContext nucleusCtx) {
        super(nucleusCtx);
        try {
            final CachingProvider cacheProvider = Caching.getCachingProvider();
            final CacheManager cacheMgr = cacheProvider.getCacheManager();
            Cache tmpcache = cacheMgr.getCache(this.cacheName);
            if (tmpcache == null) {
                final MutableConfiguration cacheConfig = new MutableConfiguration();
                final PersistenceConfiguration conf = nucleusCtx.getPersistenceConfiguration();
                if (conf.hasProperty("datanucleus.cache.level2.readThrough")) {
                    cacheConfig.setReadThrough(conf.getBooleanProperty("datanucleus.cache.level2.readThrough"));
                }
                if (conf.hasProperty("datanucleus.cache.level2.writeThrough")) {
                    cacheConfig.setWriteThrough(conf.getBooleanProperty("datanucleus.cache.level2.writeThrough"));
                }
                if (conf.hasProperty("datanucleus.cache.level2.statisticsEnabled")) {
                    cacheConfig.setStatisticsEnabled(conf.getBooleanProperty("datanucleus.cache.level2.statisticsEnabled"));
                }
                if (conf.hasProperty("datanucleus.cache.level2.storeByValue")) {
                    cacheConfig.setStoreByValue(conf.getBooleanProperty("datanucleus.cache.level2.storeByValue"));
                }
                if (this.timeout > 0L) {}
                cacheMgr.createCache(this.cacheName, (Configuration)cacheConfig);
                tmpcache = cacheMgr.getCache(this.cacheName);
            }
            this.cache = tmpcache;
        }
        catch (CacheException e) {
            throw new NucleusException("Error creating cache", (Throwable)e);
        }
    }
    
    @Override
    public void close() {
        if (this.clearAtClose) {
            this.evictAll();
        }
    }
    
    @Override
    public boolean containsOid(final Object oid) {
        return this.get(oid) != null;
    }
    
    @Override
    public CachedPC get(final Object oid) {
        return (CachedPC)this.cache.get(oid);
    }
    
    @Override
    public Map<Object, CachedPC> getAll(final Collection oids) {
        if (oids instanceof Set) {
            return (Map<Object, CachedPC>)this.cache.getAll((Set)oids);
        }
        return (Map<Object, CachedPC>)this.cache.getAll((Set)new HashSet(oids));
    }
    
    @Override
    public int getSize() {
        throw new UnsupportedOperationException("size() method not supported by this plugin");
    }
    
    @Override
    public boolean isEmpty() {
        return this.getSize() == 0;
    }
    
    @Override
    public synchronized CachedPC put(final Object oid, final CachedPC pc) {
        if (oid == null || pc == null) {
            return null;
        }
        if (this.maxSize >= 0 && this.getSize() == this.maxSize) {
            return null;
        }
        try {
            this.cache.put(oid, (Object)pc);
        }
        catch (RuntimeException re) {
            NucleusLogger.CACHE.info("Object with id " + oid + " not cached due to : " + re.getMessage());
        }
        return pc;
    }
    
    @Override
    public void putAll(final Map<Object, CachedPC> objs) {
        if (objs == null) {
            return;
        }
        try {
            this.cache.putAll((Map)objs);
        }
        catch (RuntimeException re) {
            NucleusLogger.CACHE.info("Objects not cached due to : " + re.getMessage());
        }
    }
    
    @Override
    public synchronized void evict(final Object oid) {
        this.cache.remove(oid);
    }
    
    @Override
    public synchronized void evictAll() {
        this.cache.removeAll();
    }
    
    @Override
    public synchronized void evictAll(final Collection oids) {
        if (oids == null) {
            return;
        }
        if (oids instanceof Set) {
            this.cache.removeAll((Set)oids);
        }
        else {
            this.cache.removeAll((Set)new HashSet(oids));
        }
    }
    
    @Override
    public synchronized void evictAll(final Object[] oids) {
        if (oids == null) {
            return;
        }
        final Set oidSet = new HashSet(Arrays.asList(oids));
        this.cache.removeAll(oidSet);
    }
    
    @Override
    public synchronized void evictAll(final Class pcClass, final boolean subclasses) {
        if (!this.nucleusCtx.getApiAdapter().isPersistable(pcClass)) {
            return;
        }
        this.evictAllOfClass(pcClass.getName());
        if (subclasses) {
            final String[] subclassNames = this.nucleusCtx.getMetaDataManager().getSubclassesForClass(pcClass.getName(), true);
            if (subclassNames != null) {
                for (int i = 0; i < subclassNames.length; ++i) {
                    this.evictAllOfClass(subclassNames[i]);
                }
            }
        }
    }
    
    void evictAllOfClass(final String className) {
        final AbstractClassMetaData cmd = this.nucleusCtx.getMetaDataManager().getMetaDataForClass(className, this.nucleusCtx.getClassLoaderResolver(null));
        final Iterator<Cache.Entry> entryIter = (Iterator<Cache.Entry>)this.cache.iterator();
        while (entryIter.hasNext()) {
            final Cache.Entry entry = entryIter.next();
            final Object key = entry.getKey();
            if (cmd.getIdentityType() == IdentityType.APPLICATION) {
                final String targetClassName = this.nucleusCtx.getApiAdapter().getTargetClassNameForSingleFieldIdentity(key);
                if (!className.equals(targetClassName)) {
                    continue;
                }
                entryIter.remove();
            }
            else {
                if (cmd.getIdentityType() != IdentityType.DATASTORE || !(key instanceof OID)) {
                    continue;
                }
                final OID oid = (OID)key;
                if (!className.equals(oid.getPcClass())) {
                    continue;
                }
                entryIter.remove();
            }
        }
    }
}
