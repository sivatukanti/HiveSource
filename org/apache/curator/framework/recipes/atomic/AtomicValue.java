// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.atomic;

public interface AtomicValue<T>
{
    boolean succeeded();
    
    T preValue();
    
    T postValue();
    
    AtomicStats getStats();
}
