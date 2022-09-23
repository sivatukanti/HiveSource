// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw.xact;

import org.apache.derby.iapi.store.access.TransactionInfo;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.store.raw.RawStoreFactory;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.locks.LockFactory;
import org.apache.derby.iapi.store.raw.Corruptable;

public interface TransactionFactory extends Corruptable
{
    public static final String MODULE = "org.apache.derby.iapi.store.raw.xact.TransactionFactory";
    
    LockFactory getLockFactory();
    
    Object getXAResourceManager() throws StandardException;
    
    RawTransaction startTransaction(final RawStoreFactory p0, final ContextManager p1, final String p2) throws StandardException;
    
    RawTransaction startNestedReadOnlyUserTransaction(final RawStoreFactory p0, final CompatibilitySpace p1, final ContextManager p2, final String p3) throws StandardException;
    
    RawTransaction startNestedUpdateUserTransaction(final RawStoreFactory p0, final ContextManager p1, final String p2, final boolean p3) throws StandardException;
    
    RawTransaction startGlobalTransaction(final RawStoreFactory p0, final ContextManager p1, final int p2, final byte[] p3, final byte[] p4) throws StandardException;
    
    RawTransaction findUserTransaction(final RawStoreFactory p0, final ContextManager p1, final String p2) throws StandardException;
    
    RawTransaction startNestedTopTransaction(final RawStoreFactory p0, final ContextManager p1) throws StandardException;
    
    RawTransaction startInternalTransaction(final RawStoreFactory p0, final ContextManager p1) throws StandardException;
    
    boolean findTransaction(final TransactionId p0, final RawTransaction p1);
    
    void resetTranId() throws StandardException;
    
    LogInstant firstUpdateInstant();
    
    void handlePreparedXacts(final RawStoreFactory p0) throws StandardException;
    
    void rollbackAllTransactions(final RawTransaction p0, final RawStoreFactory p1) throws StandardException;
    
    boolean submitPostCommitWork(final Serviceable p0);
    
    void setRawStoreFactory(final RawStoreFactory p0) throws StandardException;
    
    boolean noActiveUpdateTransaction();
    
    boolean hasPreparedXact();
    
    void createFinished() throws StandardException;
    
    Formatable getTransactionTable();
    
    void useTransactionTable(final Formatable p0) throws StandardException;
    
    TransactionInfo[] getTransactionInfo();
    
    boolean blockBackupBlockingOperations(final boolean p0) throws StandardException;
    
    void unblockBackupBlockingOperations();
}
