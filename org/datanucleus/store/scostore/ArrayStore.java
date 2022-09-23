// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.scostore;

import java.util.List;
import java.util.Iterator;
import org.datanucleus.state.ObjectProvider;

public interface ArrayStore extends Store
{
    Iterator iterator(final ObjectProvider p0);
    
    List getArray(final ObjectProvider p0);
    
    int size(final ObjectProvider p0);
    
    void clear(final ObjectProvider p0);
    
    boolean set(final ObjectProvider p0, final Object p1);
}
