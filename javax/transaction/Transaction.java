// 
// Decompiled by Procyon v0.5.36
// 

package javax.transaction;

import javax.transaction.xa.XAResource;

public interface Transaction
{
    void commit() throws HeuristicMixedException, HeuristicRollbackException, RollbackException, SecurityException, SystemException;
    
    boolean delistResource(final XAResource p0, final int p1) throws IllegalStateException, SystemException;
    
    boolean enlistResource(final XAResource p0) throws IllegalStateException, RollbackException, SystemException;
    
    int getStatus() throws SystemException;
    
    void registerSynchronization(final Synchronization p0) throws IllegalStateException, RollbackException, SystemException;
    
    void rollback() throws IllegalStateException, SystemException;
    
    void setRollbackOnly() throws IllegalStateException, SystemException;
}
