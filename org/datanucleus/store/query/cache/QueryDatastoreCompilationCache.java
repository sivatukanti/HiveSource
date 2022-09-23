// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query.cache;

public interface QueryDatastoreCompilationCache
{
    void close();
    
    void evict(final String p0);
    
    void clear();
    
    boolean isEmpty();
    
    int size();
    
    Object get(final String p0);
    
    Object put(final String p0, final Object p1);
    
    boolean contains(final String p0);
}
