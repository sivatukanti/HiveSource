// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import java.util.Map;
import javax.jdo.Query;
import org.datanucleus.store.query.cache.QueryResultsCache;
import java.io.Serializable;

public class JDOQueryCache implements Serializable
{
    QueryResultsCache resultsCache;
    
    public JDOQueryCache(final QueryResultsCache cache) {
        this.resultsCache = cache;
    }
    
    public QueryResultsCache getQueryCache() {
        return this.resultsCache;
    }
    
    public void evict(final Query query) {
        this.resultsCache.evict(((JDOQuery)query).getInternalQuery());
    }
    
    public void evict(final Query query, final Map params) {
        this.resultsCache.evict(((JDOQuery)query).getInternalQuery(), params);
    }
    
    public void evictAll() {
        this.resultsCache.evictAll();
    }
    
    public void pin(final Query query) {
        this.resultsCache.pin(((JDOQuery)query).getInternalQuery());
    }
    
    public void pin(final Query query, final Map params) {
        this.resultsCache.pin(((JDOQuery)query).getInternalQuery(), params);
    }
    
    public void unpin(final Query query) {
        this.resultsCache.unpin(((JDOQuery)query).getInternalQuery());
    }
    
    public void unpin(final Query query, final Map params) {
        this.resultsCache.unpin(((JDOQuery)query).getInternalQuery(), params);
    }
}
