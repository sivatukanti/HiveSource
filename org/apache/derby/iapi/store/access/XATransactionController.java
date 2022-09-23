// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.error.StandardException;

public interface XATransactionController extends TransactionController
{
    public static final int XA_RDONLY = 1;
    public static final int XA_OK = 2;
    
    void xa_commit(final boolean p0) throws StandardException;
    
    int xa_prepare() throws StandardException;
    
    void xa_rollback() throws StandardException;
}
