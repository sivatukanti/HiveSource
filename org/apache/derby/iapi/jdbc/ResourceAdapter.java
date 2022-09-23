// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import javax.transaction.xa.XAException;
import org.apache.derby.iapi.store.access.xa.XAXactId;
import org.apache.derby.iapi.store.access.xa.XAResourceManager;

public interface ResourceAdapter
{
    XAResourceManager getXAResourceManager();
    
    boolean isActive();
    
    Object findConnection(final XAXactId p0);
    
    boolean addConnection(final XAXactId p0, final Object p1);
    
    Object removeConnection(final XAXactId p0);
    
    void cancelXATransaction(final XAXactId p0, final String p1) throws XAException;
}
