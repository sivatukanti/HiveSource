// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import java.sql.SQLException;
import javax.sql.PooledConnection;
import javax.sql.ConnectionPoolDataSource;

public class BasicEmbeddedConnectionPoolDataSource40 extends BasicEmbeddedDataSource40 implements ConnectionPoolDataSource, EmbeddedConnectionPoolDataSourceInterface
{
    private static final long serialVersionUID = 7852784308039674161L;
    
    @Override
    public final PooledConnection getPooledConnection() throws SQLException {
        return this.createPooledConnection(this.getUser(), this.getPassword(), false);
    }
    
    @Override
    public final PooledConnection getPooledConnection(final String s, final String s2) throws SQLException {
        return this.createPooledConnection(s, s2, true);
    }
    
    private PooledConnection createPooledConnection(final String s, final String s2, final boolean b) throws SQLException {
        return ((Driver30)this.findDriver()).getNewPooledConnection(this, s, s2, b);
    }
}
