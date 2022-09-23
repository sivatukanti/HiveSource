// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp;

import java.sql.SQLException;
import java.sql.Connection;
import org.apache.commons.pool.ObjectPool;

public class PoolableConnection extends DelegatingConnection
{
    protected ObjectPool _pool;
    
    public PoolableConnection(final Connection conn, final ObjectPool pool) {
        super(conn);
        this._pool = null;
        this._pool = pool;
    }
    
    public PoolableConnection(final Connection conn, final ObjectPool pool, final AbandonedConfig config) {
        super(conn, config);
        this._pool = null;
        this._pool = pool;
    }
    
    @Override
    public synchronized void close() throws SQLException {
        if (this._closed) {
            return;
        }
        boolean isUnderlyingConectionClosed;
        try {
            isUnderlyingConectionClosed = this._conn.isClosed();
        }
        catch (SQLException e) {
            try {
                this._pool.invalidateObject(this);
            }
            catch (IllegalStateException ise) {
                this.passivate();
                this.getInnermostDelegate().close();
            }
            catch (Exception ex) {}
            throw (SQLException)new SQLException("Cannot close connection (isClosed check failed)").initCause(e);
        }
        if (!isUnderlyingConectionClosed) {
            try {
                this._pool.returnObject(this);
                return;
            }
            catch (IllegalStateException e4) {
                this.passivate();
                this.getInnermostDelegate().close();
                return;
            }
            catch (SQLException e) {
                throw e;
            }
            catch (RuntimeException e2) {
                throw e2;
            }
            catch (Exception e3) {
                throw (SQLException)new SQLException("Cannot close connection (return to pool failed)").initCause(e3);
            }
        }
        try {
            this._pool.invalidateObject(this);
        }
        catch (IllegalStateException e4) {
            this.passivate();
            this.getInnermostDelegate().close();
        }
        catch (Exception ex2) {}
        throw new SQLException("Already closed.");
    }
    
    public void reallyClose() throws SQLException {
        super.close();
    }
}
