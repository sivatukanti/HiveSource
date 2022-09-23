// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import org.apache.derby.impl.jdbc.EmbedCallableStatement;
import java.sql.CallableStatement;
import org.apache.derby.impl.jdbc.EmbedPreparedStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.apache.derby.impl.jdbc.Util;
import java.util.Iterator;
import javax.sql.ConnectionEvent;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.jdbc.EngineConnection;
import java.util.Collection;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.derby.iapi.jdbc.BrokeredConnection;
import org.apache.derby.impl.jdbc.EmbedConnection;
import javax.sql.ConnectionEventListener;
import java.util.ArrayList;
import org.apache.derby.iapi.jdbc.BrokeredConnectionControl;
import javax.sql.PooledConnection;

class EmbedPooledConnection implements PooledConnection, BrokeredConnectionControl
{
    private String connString;
    private ArrayList<ConnectionEventListener> eventListener;
    private int eventIterators;
    EmbedConnection realConnection;
    int defaultIsolationLevel;
    private boolean defaultReadOnly;
    BrokeredConnection currentConnectionHandle;
    final EmbeddedBaseDataSource dataSource;
    private final String username;
    private final String password;
    private final boolean requestPassword;
    protected boolean isActive;
    
    public boolean isActive() {
        return this.isActive;
    }
    
    EmbedPooledConnection(final EmbeddedBaseDataSource dataSource, final String username, final String password, final boolean requestPassword) throws SQLException {
        this.dataSource = dataSource;
        this.username = username;
        this.password = password;
        this.requestPassword = requestPassword;
        this.isActive = true;
        this.openRealConnection();
    }
    
    String getUsername() {
        if (this.username == null || this.username.equals("")) {
            return "APP";
        }
        return this.username;
    }
    
    String getPassword() {
        if (this.password == null) {
            return "";
        }
        return this.password;
    }
    
    public synchronized Connection getConnection() throws SQLException {
        this.checkActive();
        if (this.realConnection == null) {
            this.openRealConnection();
        }
        else {
            this.resetRealConnection();
        }
        this.closeCurrentConnectionHandle();
        return this.getNewCurrentConnectionHandle();
    }
    
    final void openRealConnection() throws SQLException {
        final Connection connection = this.dataSource.getConnection(this.username, this.password, this.requestPassword);
        this.realConnection = (EmbedConnection)connection;
        this.defaultIsolationLevel = connection.getTransactionIsolation();
        this.defaultReadOnly = connection.isReadOnly();
        if (this.currentConnectionHandle != null) {
            this.realConnection.setApplicationConnection(this.currentConnectionHandle);
        }
    }
    
    final Connection getNewCurrentConnectionHandle() throws SQLException {
        final BrokeredConnection brokeredConnection = ((Driver20)this.realConnection.getLocalDriver()).newBrokeredConnection(this);
        this.currentConnectionHandle = brokeredConnection;
        final BrokeredConnection applicationConnection = brokeredConnection;
        this.realConnection.setApplicationConnection(applicationConnection);
        return applicationConnection;
    }
    
    private void closeCurrentConnectionHandle() throws SQLException {
        if (this.currentConnectionHandle != null) {
            final ArrayList<ConnectionEventListener> eventListener = this.eventListener;
            this.eventListener = null;
            try {
                this.currentConnectionHandle.close();
            }
            finally {
                this.eventListener = eventListener;
            }
            this.currentConnectionHandle = null;
        }
    }
    
    void resetRealConnection() throws SQLException {
        this.realConnection.rollback();
        this.realConnection.clearWarnings();
        if (this.realConnection.getTransactionIsolation() != this.defaultIsolationLevel) {
            this.realConnection.setTransactionIsolation(this.defaultIsolationLevel);
        }
        if (!this.realConnection.getAutoCommit()) {
            this.realConnection.setAutoCommit(true);
        }
        if (this.realConnection.isReadOnly() != this.defaultReadOnly) {
            this.realConnection.setReadOnly(this.defaultReadOnly);
        }
        if (this.realConnection.getHoldability() != 1) {
            this.realConnection.setHoldability(1);
        }
        this.realConnection.resetFromPool();
    }
    
    public synchronized void close() throws SQLException {
        if (!this.isActive) {
            return;
        }
        this.closeCurrentConnectionHandle();
        try {
            if (this.realConnection != null && !this.realConnection.isClosed()) {
                this.realConnection.close();
            }
        }
        finally {
            this.realConnection = null;
            this.isActive = false;
            this.eventListener = null;
        }
    }
    
    public final synchronized void addConnectionEventListener(final ConnectionEventListener e) {
        if (!this.isActive) {
            return;
        }
        if (e == null) {
            return;
        }
        if (this.eventListener == null) {
            this.eventListener = new ArrayList<ConnectionEventListener>();
        }
        else if (this.eventIterators > 0) {
            this.eventListener = new ArrayList<ConnectionEventListener>(this.eventListener);
        }
        this.eventListener.add(e);
    }
    
    public final synchronized void removeConnectionEventListener(final ConnectionEventListener o) {
        if (o == null || this.eventListener == null) {
            return;
        }
        if (this.eventIterators > 0) {
            this.eventListener = new ArrayList<ConnectionEventListener>(this.eventListener);
        }
        this.eventListener.remove(o);
    }
    
    public synchronized EngineConnection getRealConnection() throws SQLException {
        this.checkActive();
        return this.realConnection;
    }
    
    public synchronized LanguageConnectionContext getLanguageConnection() throws SQLException {
        this.checkActive();
        return this.realConnection.getLanguageConnection();
    }
    
    public synchronized void notifyError(final SQLException ex) {
        if (ex.getErrorCode() < 40000) {
            return;
        }
        this.fireConnectionEventListeners(ex);
    }
    
    private void fireConnectionEventListeners(final SQLException ex) {
        if (this.eventListener != null && !this.eventListener.isEmpty()) {
            final ConnectionEvent connectionEvent = new ConnectionEvent(this, ex);
            ++this.eventIterators;
            try {
                for (final ConnectionEventListener connectionEventListener : this.eventListener) {
                    if (ex == null) {
                        connectionEventListener.connectionClosed(connectionEvent);
                    }
                    else {
                        connectionEventListener.connectionErrorOccurred(connectionEvent);
                    }
                }
            }
            finally {
                --this.eventIterators;
            }
        }
    }
    
    final void checkActive() throws SQLException {
        if (!this.isActive) {
            throw Util.noCurrentConnection();
        }
    }
    
    public boolean isIsolationLevelSetUsingSQLorJDBC() throws SQLException {
        return this.realConnection != null && this.realConnection.getLanguageConnection().isIsolationLevelSetUsingSQLorJDBC();
    }
    
    public void resetIsolationLevelFlag() throws SQLException {
        this.realConnection.getLanguageConnection().resetIsolationLevelFlagUsedForSQLandJDBC();
    }
    
    public boolean isInGlobalTransaction() {
        return false;
    }
    
    public void notifyException(final SQLException ex) {
        this.notifyError(ex);
    }
    
    public void checkAutoCommit(final boolean b) throws SQLException {
    }
    
    public int checkHoldCursors(final int n, final boolean b) throws SQLException {
        return n;
    }
    
    public void checkSavepoint() throws SQLException {
    }
    
    public void checkRollback() throws SQLException {
    }
    
    public void checkCommit() throws SQLException {
    }
    
    public void checkClose() throws SQLException {
        if (this.realConnection != null) {
            this.realConnection.checkForTransactionInProgress();
        }
    }
    
    public synchronized boolean closingConnection() throws SQLException {
        this.currentConnectionHandle = null;
        this.fireConnectionEventListeners(null);
        return false;
    }
    
    public Statement wrapStatement(final Statement statement) throws SQLException {
        return statement;
    }
    
    public PreparedStatement wrapStatement(final PreparedStatement preparedStatement, final String s, final Object o) throws SQLException {
        final EmbedPreparedStatement embedPreparedStatement = (EmbedPreparedStatement)preparedStatement;
        embedPreparedStatement.setBrokeredConnectionControl(this);
        return embedPreparedStatement;
    }
    
    public CallableStatement wrapStatement(final CallableStatement callableStatement, final String s) throws SQLException {
        final EmbedCallableStatement embedCallableStatement = (EmbedCallableStatement)callableStatement;
        embedCallableStatement.setBrokeredConnectionControl(this);
        return embedCallableStatement;
    }
    
    @Override
    public String toString() {
        if (this.connString == null) {
            this.connString = this.getClass().getName() + "@" + this.hashCode() + " " + "Physical Connection = " + (this.isActive ? this.realConnection.toString() : "<none>");
        }
        return this.connString;
    }
    
    public void onStatementClose(final PreparedStatement preparedStatement) {
    }
    
    public void onStatementErrorOccurred(final PreparedStatement preparedStatement, final SQLException ex) {
    }
}
