// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.connection;

import javax.transaction.xa.XAResource;

public interface ManagedConnection
{
    Object getConnection();
    
    void release();
    
    void transactionFlushed();
    
    void transactionPreClose();
    
    void close();
    
    void setCommitOnRelease(final boolean p0);
    
    void setCloseOnRelease(final boolean p0);
    
    boolean commitOnRelease();
    
    boolean closeOnRelease();
    
    XAResource getXAResource();
    
    boolean isLocked();
    
    void lock();
    
    void unlock();
    
    void addListener(final ManagedConnectionResourceListener p0);
    
    void removeListener(final ManagedConnectionResourceListener p0);
    
    boolean closeAfterTransactionEnd();
}
