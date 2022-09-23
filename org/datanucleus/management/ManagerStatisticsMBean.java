// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.management;

public interface ManagerStatisticsMBean
{
    String getRegisteredName();
    
    int getQueryActiveTotalCount();
    
    int getQueryErrorTotalCount();
    
    int getQueryExecutionTotalCount();
    
    int getQueryExecutionTimeLow();
    
    int getQueryExecutionTimeHigh();
    
    int getQueryExecutionTotalTime();
    
    int getQueryExecutionTimeAverage();
    
    int getNumberOfDatastoreWrites();
    
    int getNumberOfDatastoreReads();
    
    int getNumberOfDatastoreWritesInLatestTxn();
    
    int getNumberOfDatastoreReadsInLatestTxn();
    
    int getNumberOfObjectFetches();
    
    int getNumberOfObjectInserts();
    
    int getNumberOfObjectUpdates();
    
    int getNumberOfObjectDeletes();
    
    int getTransactionExecutionTimeAverage();
    
    int getTransactionExecutionTimeLow();
    
    int getTransactionExecutionTimeHigh();
    
    int getTransactionExecutionTotalTime();
    
    int getTransactionTotalCount();
    
    int getTransactionActiveTotalCount();
    
    int getTransactionCommittedTotalCount();
    
    int getTransactionRolledBackTotalCount();
}
