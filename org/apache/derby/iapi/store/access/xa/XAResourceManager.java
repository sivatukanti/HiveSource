// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access.xa;

import org.apache.derby.iapi.error.StandardException;
import javax.transaction.xa.Xid;
import org.apache.derby.iapi.services.context.ContextManager;

public interface XAResourceManager
{
    void commit(final ContextManager p0, final Xid p1, final boolean p2) throws StandardException;
    
    ContextManager find(final Xid p0);
    
    void forget(final ContextManager p0, final Xid p1) throws StandardException;
    
    Xid[] recover(final int p0) throws StandardException;
    
    void rollback(final ContextManager p0, final Xid p1) throws StandardException;
}
