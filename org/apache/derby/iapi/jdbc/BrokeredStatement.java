// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.Statement;
import java.sql.SQLWarning;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BrokeredStatement implements EngineStatement
{
    final BrokeredStatementControl control;
    final int resultSetType;
    final int resultSetConcurrency;
    final int resultSetHoldability;
    private String cursorName;
    private Boolean escapeProcessing;
    
    BrokeredStatement(final BrokeredStatementControl control) throws SQLException {
        this.control = control;
        this.resultSetType = this.getResultSetType();
        this.resultSetConcurrency = this.getResultSetConcurrency();
        this.resultSetHoldability = this.getResultSetHoldability();
    }
    
    public final void addBatch(final String s) throws SQLException {
        this.getStatement().addBatch(s);
    }
    
    public final void clearBatch() throws SQLException {
        this.getStatement().clearBatch();
    }
    
    public final int[] executeBatch() throws SQLException {
        return this.getStatement().executeBatch();
    }
    
    public final void cancel() throws SQLException {
        this.getStatement().cancel();
    }
    
    public final boolean execute(final String s) throws SQLException {
        return this.getStatement().execute(s);
    }
    
    public final ResultSet executeQuery(final String s) throws SQLException {
        return this.wrapResultSet(this.getStatement().executeQuery(s));
    }
    
    public final int executeUpdate(final String s) throws SQLException {
        return this.getStatement().executeUpdate(s);
    }
    
    public void close() throws SQLException {
        this.control.closeRealStatement();
    }
    
    public final Connection getConnection() throws SQLException {
        return this.getStatement().getConnection();
    }
    
    public final int getFetchDirection() throws SQLException {
        return this.getStatement().getFetchDirection();
    }
    
    public final int getFetchSize() throws SQLException {
        return this.getStatement().getFetchSize();
    }
    
    public final int getMaxFieldSize() throws SQLException {
        return this.getStatement().getMaxFieldSize();
    }
    
    public final int getMaxRows() throws SQLException {
        return this.getStatement().getMaxRows();
    }
    
    public final int getResultSetConcurrency() throws SQLException {
        return this.getStatement().getResultSetConcurrency();
    }
    
    public final void setMaxFieldSize(final int maxFieldSize) throws SQLException {
        this.getStatement().setMaxFieldSize(maxFieldSize);
    }
    
    public final void setMaxRows(final int maxRows) throws SQLException {
        this.getStatement().setMaxRows(maxRows);
    }
    
    public final void setEscapeProcessing(final boolean escapeProcessing) throws SQLException {
        this.getStatement().setEscapeProcessing(escapeProcessing);
        this.escapeProcessing = (escapeProcessing ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public final SQLWarning getWarnings() throws SQLException {
        return this.getStatement().getWarnings();
    }
    
    public final void clearWarnings() throws SQLException {
        this.getStatement().clearWarnings();
    }
    
    public final void setCursorName(final String s) throws SQLException {
        this.getStatement().setCursorName(s);
        this.cursorName = s;
    }
    
    public final ResultSet getResultSet() throws SQLException {
        return this.wrapResultSet(this.getStatement().getResultSet());
    }
    
    public final int getUpdateCount() throws SQLException {
        return this.getStatement().getUpdateCount();
    }
    
    public final boolean getMoreResults() throws SQLException {
        return this.getStatement().getMoreResults();
    }
    
    public final int getResultSetType() throws SQLException {
        return this.getStatement().getResultSetType();
    }
    
    public final void setFetchDirection(final int fetchDirection) throws SQLException {
        this.getStatement().setFetchDirection(fetchDirection);
    }
    
    public final void setFetchSize(final int fetchSize) throws SQLException {
        this.getStatement().setFetchSize(fetchSize);
    }
    
    public final int getQueryTimeout() throws SQLException {
        return this.getStatement().getQueryTimeout();
    }
    
    public final void setQueryTimeout(final int queryTimeout) throws SQLException {
        this.getStatement().setQueryTimeout(queryTimeout);
    }
    
    public final boolean execute(final String s, final int n) throws SQLException {
        return this.getStatement().execute(s, n);
    }
    
    public final boolean execute(final String s, final int[] array) throws SQLException {
        return this.getStatement().execute(s, array);
    }
    
    public final boolean execute(final String s, final String[] array) throws SQLException {
        return this.getStatement().execute(s, array);
    }
    
    public final int executeUpdate(final String s, final int n) throws SQLException {
        return this.getStatement().executeUpdate(s, n);
    }
    
    public final int executeUpdate(final String s, final int[] array) throws SQLException {
        return this.getStatement().executeUpdate(s, array);
    }
    
    public final int executeUpdate(final String s, final String[] array) throws SQLException {
        return this.getStatement().executeUpdate(s, array);
    }
    
    public final boolean getMoreResults(final int n) throws SQLException {
        return ((EngineStatement)this.getStatement()).getMoreResults(n);
    }
    
    public final ResultSet getGeneratedKeys() throws SQLException {
        return this.wrapResultSet(this.getStatement().getGeneratedKeys());
    }
    
    public final int getResultSetHoldability() throws SQLException {
        return this.controlCheck().checkHoldCursors(((EngineStatement)this.getStatement()).getResultSetHoldability());
    }
    
    public Statement createDuplicateStatement(final Connection connection, final Statement statement) throws SQLException {
        final Statement statement2 = connection.createStatement(this.resultSetType, this.resultSetConcurrency, this.resultSetHoldability);
        this.setStatementState(statement, statement2);
        return statement2;
    }
    
    void setStatementState(final Statement statement, final Statement statement2) throws SQLException {
        if (this.cursorName != null) {
            statement2.setCursorName(this.cursorName);
        }
        if (this.escapeProcessing != null) {
            statement2.setEscapeProcessing(this.escapeProcessing);
        }
        statement2.setFetchDirection(statement.getFetchDirection());
        statement2.setFetchSize(statement.getFetchSize());
        statement2.setMaxFieldSize(statement.getMaxFieldSize());
        statement2.setMaxRows(statement.getMaxRows());
        statement2.setQueryTimeout(statement.getQueryTimeout());
    }
    
    public Statement getStatement() throws SQLException {
        return this.control.getRealStatement();
    }
    
    final ResultSet wrapResultSet(final ResultSet set) {
        return this.control.wrapResultSet(this, set);
    }
    
    final BrokeredStatementControl controlCheck() throws SQLException {
        this.getStatement().getConnection();
        return this.control;
    }
    
    public boolean isWrapperFor(final Class clazz) throws SQLException {
        this.checkIfClosed();
        return clazz.isInstance(this);
    }
    
    public <T> T unwrap(final Class<T> clazz) throws SQLException {
        this.checkIfClosed();
        try {
            return clazz.cast(this);
        }
        catch (ClassCastException ex) {
            throw this.unableToUnwrap(clazz);
        }
    }
    
    public final boolean isClosed() throws SQLException {
        return ((EngineStatement)this.getStatement()).isClosed();
    }
    
    protected final void checkIfClosed() throws SQLException {
        if (this.isClosed()) {
            throw this.control.getExceptionFactory().getSQLException("XJ012.S", null, null, new Object[] { "Statement" });
        }
    }
    
    final SQLException unableToUnwrap(final Class clazz) {
        return this.control.getExceptionFactory().getSQLException("XJ128.S", null, null, new Object[] { clazz });
    }
    
    public void closeOnCompletion() throws SQLException {
        ((EngineStatement)this.getStatement()).closeOnCompletion();
    }
    
    public boolean isCloseOnCompletion() throws SQLException {
        return ((EngineStatement)this.getStatement()).isCloseOnCompletion();
    }
    
    public long[] executeLargeBatch() throws SQLException {
        return ((EngineStatement)this.getStatement()).executeLargeBatch();
    }
    
    public long executeLargeUpdate(final String s) throws SQLException {
        return ((EngineStatement)this.getStatement()).executeLargeUpdate(s);
    }
    
    public long executeLargeUpdate(final String s, final int n) throws SQLException {
        return ((EngineStatement)this.getStatement()).executeLargeUpdate(s, n);
    }
    
    public long executeLargeUpdate(final String s, final int[] array) throws SQLException {
        return ((EngineStatement)this.getStatement()).executeLargeUpdate(s, array);
    }
    
    public long executeLargeUpdate(final String s, final String[] array) throws SQLException {
        return ((EngineStatement)this.getStatement()).executeLargeUpdate(s, array);
    }
    
    public long getLargeMaxRows() throws SQLException {
        return ((EngineStatement)this.getStatement()).getLargeMaxRows();
    }
    
    public long getLargeUpdateCount() throws SQLException {
        return ((EngineStatement)this.getStatement()).getLargeUpdateCount();
    }
    
    public void setLargeMaxRows(final long largeMaxRows) throws SQLException {
        ((EngineStatement)this.getStatement()).setLargeMaxRows(largeMaxRows);
    }
}
