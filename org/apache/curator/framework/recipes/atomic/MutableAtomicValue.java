// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.atomic;

class MutableAtomicValue<T> implements AtomicValue<T>
{
    T preValue;
    T postValue;
    boolean succeeded;
    AtomicStats stats;
    
    MutableAtomicValue(final T preValue, final T postValue) {
        this(preValue, postValue, false);
    }
    
    MutableAtomicValue(final T preValue, final T postValue, final boolean succeeded) {
        this.succeeded = false;
        this.stats = new AtomicStats();
        this.preValue = preValue;
        this.postValue = postValue;
        this.succeeded = succeeded;
    }
    
    @Override
    public T preValue() {
        return this.preValue;
    }
    
    @Override
    public T postValue() {
        return this.postValue;
    }
    
    @Override
    public boolean succeeded() {
        return this.succeeded;
    }
    
    @Override
    public AtomicStats getStats() {
        return this.stats;
    }
}
