// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp.datasources;

import java.sql.SQLException;
import javax.sql.ConnectionEvent;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import javax.sql.PooledConnection;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.Map;
import org.apache.commons.pool.KeyedObjectPool;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.ConnectionEventListener;
import org.apache.commons.pool.KeyedPoolableObjectFactory;

class KeyedCPDSConnectionFactory implements KeyedPoolableObjectFactory, ConnectionEventListener, PooledConnectionManager
{
    private static final String NO_KEY_MESSAGE = "close() was called on a Connection, but I have no record of the underlying PooledConnection.";
    private final ConnectionPoolDataSource _cpds;
    private final String _validationQuery;
    private final boolean _rollbackAfterValidation;
    private final KeyedObjectPool _pool;
    private final Map validatingMap;
    private final WeakHashMap pcMap;
    
    public KeyedCPDSConnectionFactory(final ConnectionPoolDataSource cpds, final KeyedObjectPool pool, final String validationQuery) {
        this(cpds, pool, validationQuery, false);
    }
    
    public KeyedCPDSConnectionFactory(final ConnectionPoolDataSource cpds, final KeyedObjectPool pool, final String validationQuery, final boolean rollbackAfterValidation) {
        this.validatingMap = new HashMap();
        this.pcMap = new WeakHashMap();
        this._cpds = cpds;
        (this._pool = pool).setFactory(this);
        this._validationQuery = validationQuery;
        this._rollbackAfterValidation = rollbackAfterValidation;
    }
    
    public KeyedObjectPool getPool() {
        return this._pool;
    }
    
    @Override
    public synchronized Object makeObject(final Object key) throws Exception {
        Object obj = null;
        final UserPassKey upkey = (UserPassKey)key;
        PooledConnection pc = null;
        final String username = upkey.getUsername();
        final String password = upkey.getPassword();
        if (username == null) {
            pc = this._cpds.getPooledConnection();
        }
        else {
            pc = this._cpds.getPooledConnection(username, password);
        }
        if (pc == null) {
            throw new IllegalStateException("Connection pool data source returned null from getPooledConnection");
        }
        pc.addConnectionEventListener(this);
        obj = new PooledConnectionAndInfo(pc, username, password);
        this.pcMap.put(pc, obj);
        return obj;
    }
    
    @Override
    public void destroyObject(final Object key, final Object obj) throws Exception {
        if (obj instanceof PooledConnectionAndInfo) {
            final PooledConnection pc = ((PooledConnectionAndInfo)obj).getPooledConnection();
            pc.removeConnectionEventListener(this);
            this.pcMap.remove(pc);
            pc.close();
        }
    }
    
    @Override
    public boolean validateObject(final Object key, final Object obj) {
        boolean valid = false;
        if (obj instanceof PooledConnectionAndInfo) {
            final PooledConnection pconn = ((PooledConnectionAndInfo)obj).getPooledConnection();
            final String query = this._validationQuery;
            if (null != query) {
                Connection conn = null;
                Statement stmt = null;
                ResultSet rset = null;
                this.validatingMap.put(pconn, null);
                try {
                    conn = pconn.getConnection();
                    stmt = conn.createStatement();
                    rset = stmt.executeQuery(query);
                    valid = rset.next();
                    if (this._rollbackAfterValidation) {
                        conn.rollback();
                    }
                }
                catch (Exception e) {
                    valid = false;
                }
                finally {
                    if (rset != null) {
                        try {
                            rset.close();
                        }
                        catch (Throwable t) {}
                    }
                    if (stmt != null) {
                        try {
                            stmt.close();
                        }
                        catch (Throwable t2) {}
                    }
                    if (conn != null) {
                        try {
                            conn.close();
                        }
                        catch (Throwable t3) {}
                    }
                    this.validatingMap.remove(pconn);
                }
            }
            else {
                valid = true;
            }
        }
        else {
            valid = false;
        }
        return valid;
    }
    
    @Override
    public void passivateObject(final Object key, final Object obj) {
    }
    
    @Override
    public void activateObject(final Object key, final Object obj) {
    }
    
    @Override
    public void connectionClosed(final ConnectionEvent event) {
        final PooledConnection pc = (PooledConnection)event.getSource();
        if (!this.validatingMap.containsKey(pc)) {
            final PooledConnectionAndInfo info = this.pcMap.get(pc);
            if (info == null) {
                throw new IllegalStateException("close() was called on a Connection, but I have no record of the underlying PooledConnection.");
            }
            try {
                this._pool.returnObject(info.getUserPassKey(), info);
            }
            catch (Exception e4) {
                System.err.println("CLOSING DOWN CONNECTION AS IT COULD NOT BE RETURNED TO THE POOL");
                pc.removeConnectionEventListener(this);
                try {
                    this._pool.invalidateObject(info.getUserPassKey(), info);
                }
                catch (Exception e3) {
                    System.err.println("EXCEPTION WHILE DESTROYING OBJECT " + info);
                    e3.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public void connectionErrorOccurred(final ConnectionEvent event) {
        final PooledConnection pc = (PooledConnection)event.getSource();
        if (null != event.getSQLException()) {
            System.err.println("CLOSING DOWN CONNECTION DUE TO INTERNAL ERROR (" + event.getSQLException() + ")");
        }
        pc.removeConnectionEventListener(this);
        final PooledConnectionAndInfo info = this.pcMap.get(pc);
        if (info == null) {
            throw new IllegalStateException("close() was called on a Connection, but I have no record of the underlying PooledConnection.");
        }
        try {
            this._pool.invalidateObject(info.getUserPassKey(), info);
        }
        catch (Exception e) {
            System.err.println("EXCEPTION WHILE DESTROYING OBJECT " + info);
            e.printStackTrace();
        }
    }
    
    @Override
    public void invalidate(final PooledConnection pc) throws SQLException {
        final PooledConnectionAndInfo info = this.pcMap.get(pc);
        if (info == null) {
            throw new IllegalStateException("close() was called on a Connection, but I have no record of the underlying PooledConnection.");
        }
        final UserPassKey key = info.getUserPassKey();
        try {
            this._pool.invalidateObject(key, info);
            this._pool.clear(key);
        }
        catch (Exception ex) {
            throw (SQLException)new SQLException("Error invalidating connection").initCause(ex);
        }
    }
    
    @Override
    public void setPassword(final String password) {
    }
    
    @Override
    public void closePool(final String username) throws SQLException {
        try {
            this._pool.clear(new UserPassKey(username, null));
        }
        catch (Exception ex) {
            throw (SQLException)new SQLException("Error closing connection pool").initCause(ex);
        }
    }
}
