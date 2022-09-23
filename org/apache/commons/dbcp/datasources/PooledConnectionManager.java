// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp.datasources;

import java.sql.SQLException;
import javax.sql.PooledConnection;

interface PooledConnectionManager
{
    void invalidate(final PooledConnection p0) throws SQLException;
    
    void setPassword(final String p0);
    
    void closePool(final String p0) throws SQLException;
}
