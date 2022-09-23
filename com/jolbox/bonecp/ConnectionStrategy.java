// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import java.sql.SQLException;
import java.sql.Connection;

public interface ConnectionStrategy
{
    Connection getConnection() throws SQLException;
    
    Connection pollConnection();
    
    void terminateAllConnections();
    
    void cleanupConnection(final ConnectionHandle p0, final ConnectionHandle p1);
}
