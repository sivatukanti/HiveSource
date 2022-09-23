// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query.cache;

import java.util.Map;

public class AbstractQueryDatastoreCompilationCache implements QueryDatastoreCompilationCache
{
    Map<String, Object> cache;
    
    @Override
    public void clear() {
        this.cache.clear();
    }
    
    @Override
    public void close() {
        this.cache.clear();
        this.cache = null;
    }
    
    @Override
    public boolean contains(final String queryKey) {
        return this.cache.containsKey(queryKey);
    }
    
    @Override
    public void evict(final String queryKey) {
        this.cache.remove(queryKey);
    }
    
    @Override
    public Object get(final String queryKey) {
        return this.cache.get(queryKey);
    }
    
    @Override
    public boolean isEmpty() {
        return this.cache.isEmpty();
    }
    
    @Override
    public Object put(final String queryKey, final Object compilation) {
        return this.cache.put(queryKey, compilation);
    }
    
    @Override
    public int size() {
        return this.cache.size();
    }
}
