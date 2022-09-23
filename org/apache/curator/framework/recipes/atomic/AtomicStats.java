// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.atomic;

public class AtomicStats
{
    private int optimisticTries;
    private int promotedLockTries;
    private long optimisticTimeMs;
    private long promotedTimeMs;
    
    public AtomicStats() {
        this.optimisticTries = 0;
        this.promotedLockTries = 0;
        this.optimisticTimeMs = 0L;
        this.promotedTimeMs = 0L;
    }
    
    public int getOptimisticTries() {
        return this.optimisticTries;
    }
    
    public int getPromotedLockTries() {
        return this.promotedLockTries;
    }
    
    public long getOptimisticTimeMs() {
        return this.optimisticTimeMs;
    }
    
    public long getPromotedTimeMs() {
        return this.promotedTimeMs;
    }
    
    void incrementOptimisticTries() {
        ++this.optimisticTries;
    }
    
    void incrementPromotedTries() {
        ++this.promotedLockTries;
    }
    
    void setOptimisticTimeMs(final long optimisticTimeMs) {
        this.optimisticTimeMs = optimisticTimeMs;
    }
    
    void setPromotedTimeMs(final long promotedTimeMs) {
        this.promotedTimeMs = promotedTimeMs;
    }
}
