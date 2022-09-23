// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

public interface TransactionEventListener
{
    void transactionStarted();
    
    void transactionEnded();
    
    void transactionPreFlush();
    
    void transactionFlushed();
    
    void transactionPreCommit();
    
    void transactionCommitted();
    
    void transactionPreRollBack();
    
    void transactionRolledBack();
}
