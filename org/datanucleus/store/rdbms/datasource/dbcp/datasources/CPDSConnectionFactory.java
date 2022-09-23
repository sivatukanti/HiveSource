// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.datasources;

import javax.sql.ConnectionEvent;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import javax.sql.PooledConnection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.Map;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.ObjectPool;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.ConnectionEventListener;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.PoolableObjectFactory;

class CPDSConnectionFactory implements PoolableObjectFactory, ConnectionEventListener, PooledConnectionManager
{
    private static final String NO_KEY_MESSAGE = "close() was called on a Connection, but I have no record of the underlying PooledConnection.";
    private final ConnectionPoolDataSource _cpds;
    private final String _validationQuery;
    private final boolean _rollbackAfterValidation;
    private final ObjectPool _pool;
    private String _username;
    private String _password;
    private final Map validatingMap;
    private final WeakHashMap pcMap;
    
    public CPDSConnectionFactory(final ConnectionPoolDataSource cpds, final ObjectPool pool, final String validationQuery, final String username, final String password) {
        this(cpds, pool, validationQuery, false, username, password);
    }
    
    public CPDSConnectionFactory(final ConnectionPoolDataSource cpds, final ObjectPool pool, final String validationQuery, final boolean rollbackAfterValidation, final String username, final String password) {
        this._username = null;
        this._password = null;
        this.validatingMap = new HashMap();
        this.pcMap = new WeakHashMap();
        this._cpds = cpds;
        (this._pool = pool).setFactory(this);
        this._validationQuery = validationQuery;
        this._username = username;
        this._password = password;
        this._rollbackAfterValidation = rollbackAfterValidation;
    }
    
    public ObjectPool getPool() {
        return this._pool;
    }
    
    @Override
    public synchronized Object makeObject() {
        Object obj;
        try {
            PooledConnection pc = null;
            if (this._username == null) {
                pc = this._cpds.getPooledConnection();
            }
            else {
                pc = this._cpds.getPooledConnection(this._username, this._password);
            }
            if (pc == null) {
                throw new IllegalStateException("Connection pool data source returned null from getPooledConnection");
            }
            pc.addConnectionEventListener(this);
            obj = new PooledConnectionAndInfo(pc, this._username, this._password);
            this.pcMap.put(pc, obj);
        }
        catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return obj;
    }
    
    @Override
    public void destroyObject(final Object obj) throws Exception {
        if (obj instanceof PooledConnectionAndInfo) {
            final PooledConnection pc = ((PooledConnectionAndInfo)obj).getPooledConnection();
            pc.removeConnectionEventListener(this);
            this.pcMap.remove(pc);
            pc.close();
        }
    }
    
    @Override
    public boolean validateObject(final Object obj) {
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
    public void passivateObject(final Object obj) {
    }
    
    @Override
    public void activateObject(final Object obj) {
    }
    
    @Override
    public void connectionClosed(final ConnectionEvent event) {
        final PooledConnection pc = (PooledConnection)event.getSource();
        if (!this.validatingMap.containsKey(pc)) {
            final Object info = this.pcMap.get(pc);
            if (info == null) {
                throw new IllegalStateException("close() was called on a Connection, but I have no record of the underlying PooledConnection.");
            }
            try {
                this._pool.returnObject(info);
            }
            catch (Exception e3) {
                System.err.println("CLOSING DOWN CONNECTION AS IT COULD NOT BE RETURNED TO THE POOL");
                pc.removeConnectionEventListener(this);
                try {
                    this.destroyObject(info);
                }
                catch (Exception e2) {
                    System.err.println("EXCEPTION WHILE DESTROYING OBJECT " + info);
                    e2.printStackTrace();
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
        final Object info = this.pcMap.get(pc);
        if (info == null) {
            throw new IllegalStateException("close() was called on a Connection, but I have no record of the underlying PooledConnection.");
        }
        try {
            this._pool.invalidateObject(info);
        }
        catch (Exception e) {
            System.err.println("EXCEPTION WHILE DESTROYING OBJECT " + info);
            e.printStackTrace();
        }
    }
    
    @Override
    public void invalidate(final PooledConnection pc) throws SQLException {
        final Object info = this.pcMap.get(pc);
        if (info == null) {
            throw new IllegalStateException("close() was called on a Connection, but I have no record of the underlying PooledConnection.");
        }
        try {
            this._pool.invalidateObject(info);
            this._pool.close();
        }
        catch (Exception ex) {
            throw (SQLException)new SQLException("Error invalidating connection").initCause(ex);
        }
    }
    
    @Override
    public synchronized void setPassword(final String password) {
        this._password = password;
    }
    
    @Override
    public void closePool(final String username) throws SQLException {
        synchronized (this) {
            if (username == null || !username.equals(this._username)) {
                return;
            }
        }
        try {
            this._pool.close();
        }
        catch (Exception ex) {
            throw (SQLException)new SQLException("Error closing connection pool").initCause(ex);
        }
    }
}
