// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.scostore;

import java.util.Collection;
import java.util.Iterator;
import org.datanucleus.state.ObjectProvider;

public interface CollectionStore extends Store
{
    boolean hasOrderMapping();
    
    boolean updateEmbeddedElement(final ObjectProvider p0, final Object p1, final int p2, final Object p3);
    
    Iterator iterator(final ObjectProvider p0);
    
    int size(final ObjectProvider p0);
    
    boolean contains(final ObjectProvider p0, final Object p1);
    
    boolean add(final ObjectProvider p0, final Object p1, final int p2);
    
    boolean addAll(final ObjectProvider p0, final Collection p1, final int p2);
    
    boolean remove(final ObjectProvider p0, final Object p1, final int p2, final boolean p3);
    
    boolean removeAll(final ObjectProvider p0, final Collection p1, final int p2);
    
    void clear(final ObjectProvider p0);
    
    void update(final ObjectProvider p0, final Collection p1);
}
