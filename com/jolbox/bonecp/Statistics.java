// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import java.util.concurrent.atomic.AtomicLong;
import java.io.Serializable;

public class Statistics implements StatisticsMBean, Serializable
{
    private static final long serialVersionUID = -5819368300823149669L;
    private final AtomicLong cacheHits;
    private final AtomicLong cacheMiss;
    private final AtomicLong statementsCached;
    private final AtomicLong connectionsRequested;
    private final AtomicLong cumulativeConnectionWaitTime;
    private final AtomicLong cumulativeStatementExecuteTime;
    private final AtomicLong cumulativeStatementPrepareTime;
    private final AtomicLong statementsExecuted;
    private final AtomicLong statementsPrepared;
    private BoneCP pool;
    
    public Statistics(final BoneCP pool) {
        this.cacheHits = new AtomicLong(0L);
        this.cacheMiss = new AtomicLong(0L);
        this.statementsCached = new AtomicLong(0L);
        this.connectionsRequested = new AtomicLong(0L);
        this.cumulativeConnectionWaitTime = new AtomicLong(0L);
        this.cumulativeStatementExecuteTime = new AtomicLong(0L);
        this.cumulativeStatementPrepareTime = new AtomicLong(0L);
        this.statementsExecuted = new AtomicLong(0L);
        this.statementsPrepared = new AtomicLong(0L);
        this.pool = pool;
    }
    
    public void resetStats() {
        this.cacheHits.set(0L);
        this.cacheMiss.set(0L);
        this.statementsCached.set(0L);
        this.connectionsRequested.set(0L);
        this.cumulativeConnectionWaitTime.set(0L);
        this.cumulativeStatementExecuteTime.set(0L);
        this.cumulativeStatementPrepareTime.set(0L);
        this.statementsExecuted.set(0L);
        this.statementsPrepared.set(0L);
    }
    
    public double getConnectionWaitTimeAvg() {
        return (this.connectionsRequested.get() == 0L) ? 0.0 : (this.cumulativeConnectionWaitTime.get() / (1.0 * this.connectionsRequested.get()) / 1000000.0);
    }
    
    public double getStatementExecuteTimeAvg() {
        return (this.statementsExecuted.get() == 0L) ? 0.0 : (this.cumulativeStatementExecuteTime.get() / (1.0 * this.statementsExecuted.get()) / 1000000.0);
    }
    
    public double getStatementPrepareTimeAvg() {
        return (this.cumulativeStatementPrepareTime.get() == 0L) ? 0.0 : (this.cumulativeStatementPrepareTime.get() / (1.0 * this.statementsPrepared.get()) / 1000000.0);
    }
    
    public int getTotalLeased() {
        return this.pool.getTotalLeased();
    }
    
    public int getTotalFree() {
        return this.pool.getTotalFree();
    }
    
    public int getTotalCreatedConnections() {
        return this.pool.getTotalCreatedConnections();
    }
    
    public long getCacheHits() {
        return this.cacheHits.get();
    }
    
    public long getCacheMiss() {
        return this.cacheMiss.get();
    }
    
    public long getStatementsCached() {
        return this.statementsCached.get();
    }
    
    public long getConnectionsRequested() {
        return this.connectionsRequested.get();
    }
    
    public long getCumulativeConnectionWaitTime() {
        return this.cumulativeConnectionWaitTime.get() / 1000000L;
    }
    
    protected void addCumulativeConnectionWaitTime(final long increment) {
        this.cumulativeConnectionWaitTime.addAndGet(increment);
    }
    
    protected void incrementStatementsExecuted() {
        this.statementsExecuted.incrementAndGet();
    }
    
    protected void incrementStatementsPrepared() {
        this.statementsPrepared.incrementAndGet();
    }
    
    protected void incrementStatementsCached() {
        this.statementsCached.incrementAndGet();
    }
    
    protected void incrementCacheMiss() {
        this.cacheMiss.incrementAndGet();
    }
    
    protected void incrementCacheHits() {
        this.cacheHits.incrementAndGet();
    }
    
    protected void incrementConnectionsRequested() {
        this.connectionsRequested.incrementAndGet();
    }
    
    public double getCacheHitRatio() {
        return (this.cacheHits.get() + this.cacheMiss.get() == 0L) ? 0.0 : (this.cacheHits.get() / (1.0 * this.cacheHits.get() + this.cacheMiss.get()));
    }
    
    public long getStatementsExecuted() {
        return this.statementsExecuted.get();
    }
    
    public long getCumulativeStatementExecutionTime() {
        return this.cumulativeStatementExecuteTime.get() / 1000000L;
    }
    
    protected void addStatementExecuteTime(final long time) {
        this.cumulativeStatementExecuteTime.addAndGet(time);
    }
    
    protected void addStatementPrepareTime(final long time) {
        this.cumulativeStatementPrepareTime.addAndGet(time);
    }
    
    public long getCumulativeStatementPrepareTime() {
        return this.cumulativeStatementPrepareTime.get() / 1000000L;
    }
    
    public long getStatementsPrepared() {
        return this.statementsPrepared.get();
    }
}
