// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.scostore;

import org.datanucleus.state.ObjectProvider;

public interface PersistableRelationStore extends Store
{
    boolean add(final ObjectProvider p0, final ObjectProvider p1);
    
    boolean remove(final ObjectProvider p0);
    
    boolean update(final ObjectProvider p0, final ObjectProvider p1);
}
