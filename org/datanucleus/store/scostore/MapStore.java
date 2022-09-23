// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.scostore;

import java.util.Map;
import org.datanucleus.state.ObjectProvider;

public interface MapStore extends Store
{
    boolean keysAreEmbedded();
    
    boolean keysAreSerialised();
    
    boolean valuesAreEmbedded();
    
    boolean valuesAreSerialised();
    
    boolean containsValue(final ObjectProvider p0, final Object p1);
    
    boolean containsKey(final ObjectProvider p0, final Object p1);
    
    Object get(final ObjectProvider p0, final Object p1);
    
    Object put(final ObjectProvider p0, final Object p1, final Object p2);
    
    void putAll(final ObjectProvider p0, final Map p1);
    
    Object remove(final ObjectProvider p0, final Object p1);
    
    Object remove(final ObjectProvider p0, final Object p1, final Object p2);
    
    void clear(final ObjectProvider p0);
    
    SetStore keySetStore();
    
    SetStore valueSetStore();
    
    SetStore entrySetStore();
    
    boolean updateEmbeddedKey(final ObjectProvider p0, final Object p1, final int p2, final Object p3);
    
    boolean updateEmbeddedValue(final ObjectProvider p0, final Object p1, final int p2, final Object p3);
}
