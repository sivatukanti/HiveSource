// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.atomic;

public class CachedAtomicInteger
{
    private final DistributedAtomicInteger number;
    private final int cacheFactor;
    private AtomicValue<Integer> currentValue;
    private int currentIndex;
    
    public CachedAtomicInteger(final DistributedAtomicInteger number, final int cacheFactor) {
        this.currentValue = null;
        this.currentIndex = 0;
        this.number = number;
        this.cacheFactor = cacheFactor;
    }
    
    public AtomicValue<Integer> next() throws Exception {
        final MutableAtomicValue<Integer> result = new MutableAtomicValue<Integer>(0, 0);
        if (this.currentValue == null) {
            this.currentValue = this.number.add(Integer.valueOf(this.cacheFactor));
            if (!this.currentValue.succeeded()) {
                this.currentValue = null;
                result.succeeded = false;
                return result;
            }
            this.currentIndex = 0;
        }
        result.succeeded = true;
        result.preValue = this.currentValue.preValue() + this.currentIndex;
        result.postValue = result.preValue + 1;
        if (++this.currentIndex >= this.cacheFactor) {
            this.currentValue = null;
        }
        return result;
    }
}
