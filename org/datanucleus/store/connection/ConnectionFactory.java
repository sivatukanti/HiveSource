// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.connection;

import java.util.Map;
import org.datanucleus.Transaction;
import org.datanucleus.ExecutionContext;

public interface ConnectionFactory
{
    public static final String DATANUCLEUS_CONNECTION_RESOURCE_TYPE = "datanucleus.connection.resourceType";
    public static final String DATANUCLEUS_CONNECTION2_RESOURCE_TYPE = "datanucleus.connection2.resourceType";
    public static final String RESOURCE_TYPE_OPTION = "resource-type";
    
    ManagedConnection getConnection(final ExecutionContext p0, final Transaction p1, final Map p2);
    
    ManagedConnection createManagedConnection(final ExecutionContext p0, final Map p1);
    
    void close();
}
