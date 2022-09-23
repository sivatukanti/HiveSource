// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access.conglomerate;

import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.access.SortController;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.store.access.TransactionController;

public interface TransactionManager extends TransactionController
{
    public static final int MODE_NONE = 5;
    public static final int LOCK_INSTANT_DURATION = 1;
    public static final int LOCK_COMMIT_DURATION = 2;
    public static final int LOCK_MANUAL_DURATION = 3;
    
    void addPostCommitWork(final Serviceable p0);
    
    boolean checkVersion(final int p0, final int p1, final String p2) throws StandardException;
    
    void closeMe(final ScanManager p0);
    
    void closeMe(final ConglomerateController p0);
    
    void closeMe(final SortController p0);
    
    TransactionManager getInternalTransaction() throws StandardException;
    
    Transaction getRawStoreXact() throws StandardException;
}
