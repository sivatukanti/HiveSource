// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp;

import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPool;
import java.sql.CallableStatement;

public class PoolableCallableStatement extends DelegatingCallableStatement implements CallableStatement
{
    private final KeyedObjectPool _pool;
    private final Object _key;
    
    public PoolableCallableStatement(final CallableStatement stmt, final Object key, final KeyedObjectPool pool, final Connection conn) {
        super((DelegatingConnection)conn, stmt);
        this._pool = pool;
        this._key = key;
        if (this._conn != null) {
            this._conn.removeTrace(this);
        }
    }
    
    @Override
    public void close() throws SQLException {
        if (!this.isClosed()) {
            try {
                this._pool.returnObject(this._key, this);
            }
            catch (SQLException e) {
                throw e;
            }
            catch (RuntimeException e2) {
                throw e2;
            }
            catch (Exception e3) {
                throw new SQLNestedException("Cannot close CallableStatement (return to pool failed)", e3);
            }
        }
    }
    
    @Override
    protected void activate() throws SQLException {
        this._closed = false;
        if (this._conn != null) {
            this._conn.addTrace(this);
        }
        super.activate();
    }
    
    @Override
    protected void passivate() throws SQLException {
        this._closed = true;
        if (this._conn != null) {
            this._conn.removeTrace(this);
        }
        final List resultSets = this.getTrace();
        if (resultSets != null) {
            final ResultSet[] set = resultSets.toArray(new ResultSet[resultSets.size()]);
            for (int i = 0; i < set.length; ++i) {
                set[i].close();
            }
            this.clearTrace();
        }
        super.passivate();
    }
}
