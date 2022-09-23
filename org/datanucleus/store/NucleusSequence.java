// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

public interface NucleusSequence
{
    void allocate(final int p0);
    
    Object current();
    
    long currentValue();
    
    String getName();
    
    Object next();
    
    long nextValue();
}
