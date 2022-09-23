// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import org.apache.derby.iapi.jdbc.ExceptionFactory;
import org.apache.derby.impl.jdbc.EmbedResultSet;
import java.sql.ResultSet;
import org.apache.derby.iapi.jdbc.BrokeredCallableStatement;
import org.apache.derby.impl.jdbc.EmbedPreparedStatement;
import org.apache.derby.iapi.jdbc.BrokeredPreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.derby.iapi.jdbc.EngineStatement;
import org.apache.derby.impl.jdbc.EmbedStatement;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.apache.derby.impl.jdbc.EmbedConnection;
import org.apache.derby.iapi.jdbc.BrokeredStatement;
import org.apache.derby.iapi.jdbc.BrokeredConnection;
import org.apache.derby.iapi.jdbc.BrokeredStatementControl;

final class XAStatementControl implements BrokeredStatementControl
{
    private final EmbedXAConnection xaConnection;
    private final BrokeredConnection applicationConnection;
    BrokeredStatement applicationStatement;
    private EmbedConnection realConnection;
    private Statement realStatement;
    private PreparedStatement realPreparedStatement;
    private CallableStatement realCallableStatement;
    
    private XAStatementControl(final EmbedXAConnection xaConnection) {
        this.xaConnection = xaConnection;
        this.realConnection = xaConnection.realConnection;
        this.applicationConnection = xaConnection.currentConnectionHandle;
    }
    
    XAStatementControl(final EmbedXAConnection embedXAConnection, final Statement realStatement) throws SQLException {
        this(embedXAConnection);
        this.realStatement = realStatement;
        this.applicationStatement = this.applicationConnection.newBrokeredStatement(this);
        ((EmbedStatement)realStatement).setApplicationStatement(this.applicationStatement);
    }
    
    XAStatementControl(final EmbedXAConnection embedXAConnection, final PreparedStatement realPreparedStatement, final String s, final Object o) throws SQLException {
        this(embedXAConnection);
        this.realPreparedStatement = realPreparedStatement;
        this.applicationStatement = this.applicationConnection.newBrokeredStatement(this, s, o);
        ((EmbedStatement)realPreparedStatement).setApplicationStatement(this.applicationStatement);
    }
    
    XAStatementControl(final EmbedXAConnection embedXAConnection, final CallableStatement realCallableStatement, final String s) throws SQLException {
        this(embedXAConnection);
        this.realCallableStatement = realCallableStatement;
        this.applicationStatement = this.applicationConnection.newBrokeredStatement(this, s);
        ((EmbedStatement)realCallableStatement).setApplicationStatement(this.applicationStatement);
    }
    
    public void closeRealStatement() throws SQLException {
        this.realStatement.close();
    }
    
    public void closeRealCallableStatement() throws SQLException {
        this.realCallableStatement.close();
    }
    
    public void closeRealPreparedStatement() throws SQLException {
        this.realPreparedStatement.close();
    }
    
    public Statement getRealStatement() throws SQLException {
        if (this.applicationConnection == this.xaConnection.currentConnectionHandle) {
            if (this.realConnection == this.xaConnection.realConnection) {
                return this.realStatement;
            }
            if (this.xaConnection.realConnection == null) {
                this.xaConnection.getRealConnection();
            }
            final Statement duplicateStatement = this.applicationStatement.createDuplicateStatement(this.xaConnection.realConnection, this.realStatement);
            ((EmbedStatement)this.realStatement).transferBatch((EmbedStatement)duplicateStatement);
            try {
                this.realStatement.close();
            }
            catch (SQLException ex) {}
            this.realStatement = duplicateStatement;
            this.realConnection = this.xaConnection.realConnection;
            ((EmbedStatement)this.realStatement).setApplicationStatement(this.applicationStatement);
        }
        return this.realStatement;
    }
    
    public PreparedStatement getRealPreparedStatement() throws SQLException {
        if (this.applicationConnection == this.xaConnection.currentConnectionHandle) {
            if (this.realConnection == this.xaConnection.realConnection) {
                return this.realPreparedStatement;
            }
            if (this.xaConnection.realConnection == null) {
                this.xaConnection.getRealConnection();
            }
            final PreparedStatement duplicateStatement = ((BrokeredPreparedStatement)this.applicationStatement).createDuplicateStatement(this.xaConnection.realConnection, this.realPreparedStatement);
            ((EmbedPreparedStatement)this.realPreparedStatement).transferParameters((EmbedPreparedStatement)duplicateStatement);
            try {
                this.realPreparedStatement.close();
            }
            catch (SQLException ex) {}
            this.realPreparedStatement = duplicateStatement;
            this.realConnection = this.xaConnection.realConnection;
            ((EmbedStatement)this.realPreparedStatement).setApplicationStatement(this.applicationStatement);
        }
        return this.realPreparedStatement;
    }
    
    public CallableStatement getRealCallableStatement() throws SQLException {
        if (this.applicationConnection == this.xaConnection.currentConnectionHandle) {
            if (this.realConnection == this.xaConnection.realConnection) {
                return this.realCallableStatement;
            }
            if (this.xaConnection.realConnection == null) {
                this.xaConnection.getRealConnection();
            }
            final CallableStatement duplicateStatement = ((BrokeredCallableStatement)this.applicationStatement).createDuplicateStatement(this.xaConnection.realConnection, this.realCallableStatement);
            ((EmbedStatement)this.realCallableStatement).transferBatch((EmbedStatement)duplicateStatement);
            try {
                this.realCallableStatement.close();
            }
            catch (SQLException ex) {}
            this.realCallableStatement = duplicateStatement;
            this.realConnection = this.xaConnection.realConnection;
            ((EmbedStatement)this.realCallableStatement).setApplicationStatement(this.applicationStatement);
        }
        return this.realCallableStatement;
    }
    
    public ResultSet wrapResultSet(final Statement applicationStatement, final ResultSet set) {
        if (set != null) {
            ((EmbedResultSet)set).setApplicationStatement(applicationStatement);
        }
        return set;
    }
    
    public int checkHoldCursors(final int n) throws SQLException {
        return this.xaConnection.checkHoldCursors(n, true);
    }
    
    public ExceptionFactory getExceptionFactory() {
        return this.applicationConnection.getExceptionFactory();
    }
}
