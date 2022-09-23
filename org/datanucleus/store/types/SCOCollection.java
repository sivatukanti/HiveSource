// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types;

public interface SCOCollection extends SCOContainer
{
    void updateEmbeddedElement(final Object p0, final int p1, final Object p2);
    
    boolean remove(final Object p0, final boolean p1);
}
