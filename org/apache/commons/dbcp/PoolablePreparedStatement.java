// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp;

import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import org.apache.commons.pool.KeyedObjectPool;
import java.sql.PreparedStatement;

public class PoolablePreparedStatement extends DelegatingPreparedStatement implements PreparedStatement
{
    protected KeyedObjectPool _pool;
    protected Object _key;
    private volatile boolean batchAdded;
    
    public PoolablePreparedStatement(final PreparedStatement stmt, final Object key, final KeyedObjectPool pool, final Connection conn) {
        super((DelegatingConnection)conn, stmt);
        this._pool = null;
        this._key = null;
        this.batchAdded = false;
        this._pool = pool;
        this._key = key;
        if (this._conn != null) {
            this._conn.removeTrace(this);
        }
    }
    
    @Override
    public void addBatch() throws SQLException {
        super.addBatch();
        this.batchAdded = true;
    }
    
    @Override
    public void clearBatch() throws SQLException {
        this.batchAdded = false;
        super.clearBatch();
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
                throw new SQLNestedException("Cannot close preparedstatement (return to pool failed)", e3);
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
        if (this.batchAdded) {
            this.clearBatch();
        }
        super.passivate();
    }
}
