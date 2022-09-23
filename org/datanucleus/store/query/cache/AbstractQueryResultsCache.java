// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query.cache;

import org.datanucleus.query.QueryUtils;
import org.datanucleus.store.query.Query;
import java.util.Iterator;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.util.NucleusLogger;
import java.util.HashMap;
import org.datanucleus.NucleusContext;
import java.util.List;
import java.util.Map;
import java.util.HashSet;

public class AbstractQueryResultsCache implements QueryResultsCache
{
    HashSet<String> keysToPin;
    Map<String, List<Object>> pinnedCache;
    Map<String, List<Object>> cache;
    private int maxSize;
    private final NucleusContext nucCtx;
    
    public AbstractQueryResultsCache(final NucleusContext nucleusCtx) {
        this.keysToPin = new HashSet<String>();
        this.pinnedCache = new HashMap<String, List<Object>>();
        this.cache = null;
        this.maxSize = -1;
        this.maxSize = nucleusCtx.getPersistenceConfiguration().getIntProperty("datanucleus.cache.queryResults.maxSize");
        this.nucCtx = nucleusCtx;
    }
    
    @Override
    public void close() {
        this.cache.clear();
        this.cache = null;
        this.pinnedCache.clear();
        this.pinnedCache = null;
    }
    
    @Override
    public boolean contains(final String queryKey) {
        return this.cache.containsKey(queryKey);
    }
    
    @Override
    public synchronized void evict(final Class candidate) {
        final AbstractClassMetaData cmd = this.nucCtx.getMetaDataManager().getMetaDataForClass(candidate, this.nucCtx.getClassLoaderResolver(candidate.getClassLoader()));
        final Iterator<String> iter = this.cache.keySet().iterator();
        while (iter.hasNext()) {
            final String key = iter.next();
            if (key.matches("JDOQL:.* FROM " + candidate.getName() + ".*")) {
                NucleusLogger.GENERAL.info(">> Evicting query results for key=" + key);
                iter.remove();
            }
            else if (key.matches("JPQL:.* FROM " + candidate.getName() + ".*")) {
                NucleusLogger.GENERAL.info(">> Evicting query results for key=" + key);
                iter.remove();
            }
            else {
                if (!key.matches("JPQL:.* FROM " + cmd.getEntityName() + ".*")) {
                    continue;
                }
                NucleusLogger.GENERAL.info(">> Evicting query results for key=" + key);
                iter.remove();
            }
        }
    }
    
    @Override
    public synchronized void evictAll() {
        this.cache.clear();
    }
    
    @Override
    public synchronized void evict(final Query query) {
        final String baseKey = QueryUtils.getKeyForQueryResultsCache(query, null);
        Iterator<String> iter = this.cache.keySet().iterator();
        while (iter.hasNext()) {
            final String key = iter.next();
            if (key.startsWith(baseKey)) {
                iter.remove();
            }
        }
        iter = this.pinnedCache.keySet().iterator();
        while (iter.hasNext()) {
            final String key = iter.next();
            if (key.startsWith(baseKey)) {
                iter.remove();
            }
        }
    }
    
    @Override
    public synchronized void evict(final Query query, final Map params) {
        final String key = QueryUtils.getKeyForQueryResultsCache(query, params);
        this.cache.remove(key);
        this.pinnedCache.remove(key);
    }
    
    @Override
    public void pin(final Query query, final Map params) {
        final String key = QueryUtils.getKeyForQueryResultsCache(query, params);
        final List<Object> results = this.cache.get(key);
        if (results != null) {
            this.keysToPin.add(key);
            this.pinnedCache.put(key, results);
            this.cache.remove(key);
        }
    }
    
    @Override
    public void pin(final Query query) {
        final String key = QueryUtils.getKeyForQueryResultsCache(query, null);
        final List<Object> results = this.cache.get(key);
        if (results != null) {
            this.keysToPin.add(key);
            this.pinnedCache.put(key, results);
            this.cache.remove(key);
        }
    }
    
    @Override
    public void unpin(final Query query, final Map params) {
        final String key = QueryUtils.getKeyForQueryResultsCache(query, params);
        final List<Object> results = this.pinnedCache.get(key);
        if (results != null) {
            this.keysToPin.remove(key);
            this.cache.put(key, results);
            this.pinnedCache.remove(key);
        }
    }
    
    @Override
    public void unpin(final Query query) {
        final String key = QueryUtils.getKeyForQueryResultsCache(query, null);
        final List<Object> results = this.pinnedCache.get(key);
        if (results != null) {
            this.keysToPin.remove(key);
            this.cache.put(key, results);
            this.pinnedCache.remove(key);
        }
    }
    
    @Override
    public List<Object> get(final String queryKey) {
        if (this.pinnedCache.containsKey(queryKey)) {
            return this.pinnedCache.get(queryKey);
        }
        return this.cache.get(queryKey);
    }
    
    @Override
    public boolean isEmpty() {
        return this.cache.isEmpty();
    }
    
    @Override
    public synchronized List<Object> put(final String queryKey, final List<Object> results) {
        if (this.maxSize >= 0 && this.size() == this.maxSize) {
            return null;
        }
        if (this.keysToPin.contains(queryKey)) {
            return this.pinnedCache.put(queryKey, results);
        }
        return this.cache.put(queryKey, results);
    }
    
    @Override
    public int size() {
        return this.cache.size();
    }
}
