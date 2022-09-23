// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.atomic;

public interface DistributedAtomicNumber<T>
{
    AtomicValue<T> get() throws Exception;
    
    AtomicValue<T> compareAndSet(final T p0, final T p1) throws Exception;
    
    AtomicValue<T> trySet(final T p0) throws Exception;
    
    boolean initialize(final T p0) throws Exception;
    
    void forceSet(final T p0) throws Exception;
    
    AtomicValue<T> increment() throws Exception;
    
    AtomicValue<T> decrement() throws Exception;
    
    AtomicValue<T> add(final T p0) throws Exception;
    
    AtomicValue<T> subtract(final T p0) throws Exception;
}
