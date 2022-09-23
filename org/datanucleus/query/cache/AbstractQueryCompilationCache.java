// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.cache;

import org.datanucleus.query.compiler.QueryCompilation;
import java.util.Map;

public class AbstractQueryCompilationCache
{
    Map<String, QueryCompilation> cache;
    
    public void clear() {
        this.cache.clear();
    }
    
    public void close() {
        this.cache.clear();
        this.cache = null;
    }
    
    public boolean contains(final String queryKey) {
        return this.cache.containsKey(queryKey);
    }
    
    public void evict(final String queryKey) {
        this.cache.remove(queryKey);
    }
    
    public QueryCompilation get(final String queryKey) {
        return this.cache.get(queryKey);
    }
    
    public boolean isEmpty() {
        return this.cache.isEmpty();
    }
    
    public QueryCompilation put(final String queryKey, final QueryCompilation compilation) {
        return this.cache.put(queryKey, compilation);
    }
    
    public int size() {
        return this.cache.size();
    }
}
