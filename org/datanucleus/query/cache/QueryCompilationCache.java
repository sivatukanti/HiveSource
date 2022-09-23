// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.cache;

import org.datanucleus.query.compiler.QueryCompilation;

public interface QueryCompilationCache
{
    void close();
    
    void evict(final String p0);
    
    void clear();
    
    boolean isEmpty();
    
    int size();
    
    QueryCompilation get(final String p0);
    
    QueryCompilation put(final String p0, final QueryCompilation p1);
    
    boolean contains(final String p0);
}
