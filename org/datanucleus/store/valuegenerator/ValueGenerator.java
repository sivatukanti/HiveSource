// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

public interface ValueGenerator
{
    String getName();
    
    Object next();
    
    void allocate(final int p0);
    
    Object current();
    
    long nextValue();
    
    long currentValue();
}
