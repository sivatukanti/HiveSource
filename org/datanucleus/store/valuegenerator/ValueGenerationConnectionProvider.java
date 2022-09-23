// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import org.datanucleus.store.connection.ManagedConnection;

public interface ValueGenerationConnectionProvider
{
    ManagedConnection retrieveConnection();
    
    void releaseConnection();
}
