// 
// Decompiled by Procyon v0.5.36
// 

package javax.transaction;

public interface TransactionSynchronizationRegistry
{
    Object getResource(final Object p0);
    
    boolean getRollbackOnly();
    
    Object getTransactionKey();
    
    int getTransactionStatus();
    
    void putResource(final Object p0, final Object p1);
    
    void registerInterposedSynchronization(final Synchronization p0);
    
    void setRollbackOnly();
}
