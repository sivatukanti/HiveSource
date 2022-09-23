// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.connection;

import java.util.Map;
import org.datanucleus.Transaction;
import org.datanucleus.ExecutionContext;

public interface ConnectionManager
{
    ConnectionFactory lookupConnectionFactory(final String p0);
    
    void registerConnectionFactory(final String p0, final ConnectionFactory p1);
    
    void closeAllConnections(final ConnectionFactory p0, final ExecutionContext p1);
    
    ManagedConnection allocateConnection(final ConnectionFactory p0, final ExecutionContext p1, final Transaction p2, final Map p3);
    
    void disableConnectionPool();
}
