// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.locks.LockFactory;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.conglomerate.MethodFactory;

public interface AccessFactory
{
    public static final String MODULE = "org.apache.derby.iapi.store.access.AccessFactory";
    
    void registerAccessMethod(final MethodFactory p0);
    
    void createFinished() throws StandardException;
    
    MethodFactory findMethodFactoryByImpl(final String p0) throws StandardException;
    
    MethodFactory findMethodFactoryByFormat(final UUID p0);
    
    LockFactory getLockFactory();
    
    Object getXAResourceManager() throws StandardException;
    
    boolean isReadOnly();
    
    void createReadMeFiles() throws StandardException;
    
    TransactionController getTransaction(final ContextManager p0) throws StandardException;
    
    TransactionController getAndNameTransaction(final ContextManager p0, final String p1) throws StandardException;
    
    TransactionInfo[] getTransactionInfo();
    
    Object startXATransaction(final ContextManager p0, final int p1, final byte[] p2, final byte[] p3) throws StandardException;
    
    void startReplicationMaster(final String p0, final String p1, final int p2, final String p3) throws StandardException;
    
    void stopReplicationMaster() throws StandardException;
    
    void failover(final String p0) throws StandardException;
    
    void freeze() throws StandardException;
    
    void unfreeze() throws StandardException;
    
    void backup(final String p0, final boolean p1) throws StandardException;
    
    void backupAndEnableLogArchiveMode(final String p0, final boolean p1, final boolean p2) throws StandardException;
    
    void disableLogArchiveMode(final boolean p0) throws StandardException;
    
    void checkpoint() throws StandardException;
    
    void waitForPostCommitToFinishWork();
}
