// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

public interface StatisticsMBean
{
    double getConnectionWaitTimeAvg();
    
    double getStatementExecuteTimeAvg();
    
    double getStatementPrepareTimeAvg();
    
    int getTotalLeased();
    
    int getTotalFree();
    
    int getTotalCreatedConnections();
    
    long getCacheHits();
    
    long getCacheMiss();
    
    long getStatementsCached();
    
    long getStatementsPrepared();
    
    long getConnectionsRequested();
    
    long getCumulativeConnectionWaitTime();
    
    long getCumulativeStatementExecutionTime();
    
    long getCumulativeStatementPrepareTime();
    
    void resetStats();
    
    double getCacheHitRatio();
    
    long getStatementsExecuted();
}
