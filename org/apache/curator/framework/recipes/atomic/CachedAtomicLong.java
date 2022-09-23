// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.atomic;

public class CachedAtomicLong
{
    private final DistributedAtomicLong number;
    private final long cacheFactor;
    private AtomicValue<Long> currentValue;
    private int currentIndex;
    
    public CachedAtomicLong(final DistributedAtomicLong number, final int cacheFactor) {
        this.currentValue = null;
        this.currentIndex = 0;
        this.number = number;
        this.cacheFactor = cacheFactor;
    }
    
    public AtomicValue<Long> next() throws Exception {
        final MutableAtomicValue<Long> result = new MutableAtomicValue<Long>(0L, 0L);
        if (this.currentValue == null) {
            this.currentValue = this.number.add(Long.valueOf(this.cacheFactor));
            if (!this.currentValue.succeeded()) {
                this.currentValue = null;
                result.succeeded = false;
                return result;
            }
            this.currentIndex = 0;
        }
        result.succeeded = true;
        result.preValue = this.currentValue.preValue() + this.currentIndex;
        result.postValue = result.preValue + 1L;
        if (++this.currentIndex >= this.cacheFactor) {
            this.currentValue = null;
        }
        return result;
    }
}
