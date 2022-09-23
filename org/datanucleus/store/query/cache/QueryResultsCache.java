// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query.cache;

import java.util.List;
import java.util.Map;
import org.datanucleus.store.query.Query;
import java.io.Serializable;

public interface QueryResultsCache extends Serializable
{
    void close();
    
    void evict(final Class p0);
    
    void evict(final Query p0);
    
    void evict(final Query p0, final Map p1);
    
    void evictAll();
    
    void pin(final Query p0);
    
    void pin(final Query p0, final Map p1);
    
    void unpin(final Query p0);
    
    void unpin(final Query p0, final Map p1);
    
    boolean isEmpty();
    
    int size();
    
    List<Object> get(final String p0);
    
    List<Object> put(final String p0, final List<Object> p1);
    
    boolean contains(final String p0);
}
