// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.store.access.FileResource;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.store.access.RowSource;
import java.util.Properties;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.services.context.ContextManager;

public interface Transaction
{
    public static final int RELEASE_LOCKS = 1;
    public static final int KEEP_LOCKS = 2;
    public static final int XA_RDONLY = 1;
    public static final int XA_OK = 2;
    
    ContextManager getContextManager();
    
    CompatibilitySpace getCompatibilitySpace();
    
    void setNoLockWait(final boolean p0);
    
    void setup(final PersistentSet p0) throws StandardException;
    
    GlobalTransactionId getGlobalId();
    
    LockingPolicy getDefaultLockingPolicy();
    
    LockingPolicy newLockingPolicy(final int p0, final int p1, final boolean p2);
    
    void setDefaultLockingPolicy(final LockingPolicy p0);
    
    LogInstant commit() throws StandardException;
    
    LogInstant commitNoSync(final int p0) throws StandardException;
    
    void abort() throws StandardException;
    
    void close() throws StandardException;
    
    void destroy() throws StandardException;
    
    int setSavePoint(final String p0, final Object p1) throws StandardException;
    
    int releaseSavePoint(final String p0, final Object p1) throws StandardException;
    
    int rollbackToSavePoint(final String p0, final Object p1) throws StandardException;
    
    ContainerHandle openContainer(final ContainerKey p0, final int p1) throws StandardException;
    
    ContainerHandle openContainer(final ContainerKey p0, final LockingPolicy p1, final int p2) throws StandardException;
    
    long addContainer(final long p0, final long p1, final int p2, final Properties p3, final int p4) throws StandardException;
    
    void dropContainer(final ContainerKey p0) throws StandardException;
    
    long addAndLoadStreamContainer(final long p0, final Properties p1, final RowSource p2) throws StandardException;
    
    StreamContainerHandle openStreamContainer(final long p0, final long p1, final boolean p2) throws StandardException;
    
    void dropStreamContainer(final long p0, final long p1) throws StandardException;
    
    void logAndDo(final Loggable p0) throws StandardException;
    
    void addPostCommitWork(final Serviceable p0);
    
    void addPostTerminationWork(final Serviceable p0);
    
    boolean isIdle();
    
    boolean isPristine();
    
    FileResource getFileHandler();
    
    boolean anyoneBlocked();
    
    void createXATransactionFromLocalTransaction(final int p0, final byte[] p1, final byte[] p2) throws StandardException;
    
    void xa_commit(final boolean p0) throws StandardException;
    
    int xa_prepare() throws StandardException;
    
    void xa_rollback() throws StandardException;
    
    String getActiveStateTxIdString();
    
    DataValueFactory getDataValueFactory() throws StandardException;
}
