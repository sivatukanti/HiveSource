// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.cache;

import java.util.List;
import java.util.Iterator;
import java.util.Map;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.store.query.Query;
import javax.cache.configuration.Configuration;
import javax.cache.CacheManager;
import javax.cache.spi.CachingProvider;
import org.datanucleus.PersistenceConfiguration;
import javax.cache.CacheException;
import org.datanucleus.exceptions.NucleusException;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.Caching;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.NucleusContext;
import javax.cache.Cache;
import org.datanucleus.store.query.cache.QueryResultsCache;

public class JavaxCacheQueryResultCache implements QueryResultsCache
{
    private Cache cache;
    
    public JavaxCacheQueryResultCache(final NucleusContext nucleusCtx) {
        final PersistenceConfiguration conf = nucleusCtx.getPersistenceConfiguration();
        String cacheName = conf.getStringProperty("datanucleus.cache.queryResults.cacheName");
        if (cacheName == null) {
            NucleusLogger.CACHE.warn("No 'datanucleus.cache.queryResults.cacheName' specified so using name of 'DataNucleus-Query'");
            cacheName = "datanucleus-query";
        }
        try {
            final CachingProvider cacheProvider = Caching.getCachingProvider();
            final CacheManager cacheMgr = cacheProvider.getCacheManager();
            Cache tmpcache = cacheMgr.getCache(cacheName);
            if (tmpcache == null) {
                final Configuration cacheConfig = (Configuration)new MutableConfiguration();
                cacheMgr.createCache(cacheName, cacheConfig);
                tmpcache = cacheMgr.getCache(cacheName);
            }
            this.cache = tmpcache;
        }
        catch (CacheException e) {
            throw new NucleusException("Error creating cache", (Throwable)e);
        }
    }
    
    @Override
    public void close() {
        this.evictAll();
        this.cache = null;
    }
    
    @Override
    public boolean contains(final String queryKey) {
        return this.get(queryKey) != null;
    }
    
    @Override
    public void evict(final Class candidate) {
    }
    
    @Override
    public synchronized void evict(final Query query) {
        final String baseKey = QueryUtils.getKeyForQueryResultsCache(query, null);
        final Iterator<Cache.Entry> entryIter = (Iterator<Cache.Entry>)this.cache.iterator();
        while (entryIter.hasNext()) {
            final Cache.Entry entry = entryIter.next();
            final String key = (String)entry.getKey();
            if (key.startsWith(baseKey)) {
                entryIter.remove();
            }
        }
    }
    
    @Override
    public synchronized void evict(final Query query, final Map params) {
        final String key = QueryUtils.getKeyForQueryResultsCache(query, params);
        this.cache.remove((Object)key);
    }
    
    @Override
    public synchronized void evictAll() {
        this.cache.removeAll();
    }
    
    @Override
    public void pin(final Query query, final Map params) {
        throw new UnsupportedOperationException("This cache doesn't support pinning/unpinning");
    }
    
    @Override
    public void pin(final Query query) {
        throw new UnsupportedOperationException("This cache doesn't support pinning/unpinning");
    }
    
    @Override
    public void unpin(final Query query, final Map params) {
        throw new UnsupportedOperationException("This cache doesn't support pinning/unpinning");
    }
    
    @Override
    public void unpin(final Query query) {
        throw new UnsupportedOperationException("This cache doesn't support pinning/unpinning");
    }
    
    @Override
    public List<Object> get(final String queryKey) {
        return (List<Object>)this.cache.get((Object)queryKey);
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public synchronized List<Object> put(final String queryKey, final List<Object> results) {
        if (queryKey == null || results == null) {
            return null;
        }
        try {
            this.cache.put((Object)queryKey, (Object)results);
        }
        catch (RuntimeException re) {
            NucleusLogger.CACHE.info("Query results with key '" + queryKey + "' not cached. " + re.getMessage());
        }
        return results;
    }
    
    @Override
    public int size() {
        throw new UnsupportedOperationException("size() method not supported by this plugin");
    }
}
