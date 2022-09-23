// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.scostore;

import java.util.ListIterator;
import java.util.List;
import java.util.Collection;
import org.datanucleus.state.ObjectProvider;

public interface ListStore extends CollectionStore
{
    void add(final ObjectProvider p0, final Object p1, final int p2, final int p3);
    
    boolean addAll(final ObjectProvider p0, final Collection p1, final int p2, final int p3);
    
    Object remove(final ObjectProvider p0, final int p1, final int p2);
    
    Object get(final ObjectProvider p0, final int p1);
    
    Object set(final ObjectProvider p0, final int p1, final Object p2, final boolean p3);
    
    List subList(final ObjectProvider p0, final int p1, final int p2);
    
    int indexOf(final ObjectProvider p0, final Object p1);
    
    int lastIndexOf(final ObjectProvider p0, final Object p1);
    
    ListIterator listIterator(final ObjectProvider p0);
}
