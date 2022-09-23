// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping;

import org.datanucleus.state.ObjectProvider;

public interface MappingCallbacks
{
    void insertPostProcessing(final ObjectProvider p0);
    
    void postInsert(final ObjectProvider p0);
    
    void postFetch(final ObjectProvider p0);
    
    void postUpdate(final ObjectProvider p0);
    
    void preDelete(final ObjectProvider p0);
}
