// 
// Decompiled by Procyon v0.5.36
// 

package javax.transaction;

public interface UserTransaction
{
    void begin() throws NotSupportedException, SystemException;
    
    void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException, SecurityException, SystemException;
    
    int getStatus() throws SystemException;
    
    void rollback() throws IllegalStateException, SecurityException, SystemException;
    
    void setRollbackOnly() throws IllegalStateException, SystemException;
    
    void setTransactionTimeout(final int p0) throws SystemException;
}
