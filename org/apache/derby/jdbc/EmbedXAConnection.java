// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import org.apache.derby.iapi.jdbc.EngineConnection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import org.apache.derby.impl.jdbc.Util;
import javax.transaction.xa.XAResource;
import java.sql.SQLException;
import org.apache.derby.iapi.jdbc.ResourceAdapter;
import javax.sql.XAConnection;

class EmbedXAConnection extends EmbedPooledConnection implements XAConnection
{
    private EmbedXAResource xaRes;
    
    EmbedXAConnection(final EmbeddedBaseDataSource embeddedBaseDataSource, final ResourceAdapter resourceAdapter, final String s, final String s2, final boolean b) throws SQLException {
        super(embeddedBaseDataSource, s, s2, b);
        this.xaRes = new EmbedXAResource(this, resourceAdapter);
    }
    
    @Override
    public boolean isInGlobalTransaction() {
        return this.isGlobal();
    }
    
    private boolean isGlobal() {
        return this.xaRes.getCurrentXid() != null;
    }
    
    public final synchronized XAResource getXAResource() throws SQLException {
        this.checkActive();
        return this.xaRes;
    }
    
    @Override
    public void checkAutoCommit(final boolean b) throws SQLException {
        if (b && this.isGlobal()) {
            throw Util.generateCsSQLException("XJ056.S");
        }
        super.checkAutoCommit(b);
    }
    
    @Override
    public int checkHoldCursors(int n, final boolean b) throws SQLException {
        if (n == 1 && this.isGlobal()) {
            if (!b) {
                throw Util.generateCsSQLException("XJ05C.S");
            }
            n = 2;
        }
        return super.checkHoldCursors(n, b);
    }
    
    @Override
    public void checkSavepoint() throws SQLException {
        if (this.isGlobal()) {
            throw Util.generateCsSQLException("XJ058.S");
        }
        super.checkSavepoint();
    }
    
    @Override
    public void checkRollback() throws SQLException {
        if (this.isGlobal()) {
            throw Util.generateCsSQLException("XJ058.S");
        }
        super.checkRollback();
    }
    
    @Override
    public void checkCommit() throws SQLException {
        if (this.isGlobal()) {
            throw Util.generateCsSQLException("XJ057.S");
        }
        super.checkCommit();
    }
    
    @Override
    public void checkClose() throws SQLException {
        if (!this.isGlobal()) {
            super.checkClose();
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        Connection connection;
        if (!this.isGlobal()) {
            connection = super.getConnection();
        }
        else {
            if (this.currentConnectionHandle != null) {
                throw Util.generateCsSQLException("XJ059.S");
            }
            connection = this.getNewCurrentConnectionHandle();
        }
        this.currentConnectionHandle.syncState();
        return connection;
    }
    
    @Override
    public Statement wrapStatement(final Statement statement) throws SQLException {
        return new XAStatementControl(this, statement).applicationStatement;
    }
    
    @Override
    public PreparedStatement wrapStatement(PreparedStatement wrapStatement, final String s, final Object o) throws SQLException {
        wrapStatement = super.wrapStatement(wrapStatement, s, o);
        return (PreparedStatement)new XAStatementControl(this, wrapStatement, s, o).applicationStatement;
    }
    
    @Override
    public CallableStatement wrapStatement(CallableStatement wrapStatement, final String s) throws SQLException {
        wrapStatement = super.wrapStatement(wrapStatement, s);
        return (CallableStatement)new XAStatementControl(this, wrapStatement, s).applicationStatement;
    }
    
    @Override
    public EngineConnection getRealConnection() throws SQLException {
        final EngineConnection realConnection = super.getRealConnection();
        if (realConnection != null) {
            return realConnection;
        }
        this.openRealConnection();
        this.currentConnectionHandle.setState(true);
        return this.realConnection;
    }
}
