// 
// Decompiled by Procyon v0.5.36
// 

package javax.transaction;

public interface TransactionManager
{
    void begin() throws NotSupportedException, SystemException;
    
    void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException, SecurityException, SystemException;
    
    int getStatus() throws SystemException;
    
    Transaction getTransaction() throws SystemException;
    
    void resume(final Transaction p0) throws IllegalStateException, InvalidTransactionException, SystemException;
    
    void rollback() throws IllegalStateException, SecurityException, SystemException;
    
    void setRollbackOnly() throws IllegalStateException, SystemException;
    
    void setTransactionTimeout(final int p0) throws SystemException;
    
    Transaction suspend() throws SystemException;
}
