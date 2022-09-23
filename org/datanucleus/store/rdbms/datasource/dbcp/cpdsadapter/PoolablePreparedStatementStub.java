// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.cpdsadapter;

import java.sql.SQLException;
import java.sql.Connection;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPool;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.datasource.dbcp.PoolablePreparedStatement;

class PoolablePreparedStatementStub extends PoolablePreparedStatement
{
    public PoolablePreparedStatementStub(final PreparedStatement stmt, final Object key, final KeyedObjectPool pool, final Connection conn) {
        super(stmt, key, pool, conn);
    }
    
    @Override
    protected void activate() throws SQLException {
        super.activate();
    }
    
    @Override
    protected void passivate() throws SQLException {
        super.passivate();
    }
}
