// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import org.apache.derby.iapi.error.SQLWarningFactory;
import java.sql.Savepoint;
import java.util.Map;
import java.sql.DatabaseMetaData;
import java.sql.SQLWarning;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public class BrokeredConnection implements EngineConnection
{
    int stateHoldability;
    final BrokeredConnectionControl control;
    protected boolean isClosed;
    private String connString;
    private final ExceptionFactory exceptionFactory;
    private int stateIsolationLevel;
    private boolean stateReadOnly;
    private boolean stateAutoCommit;
    
    public BrokeredConnection(final BrokeredConnectionControl control) throws SQLException {
        this.stateHoldability = 1;
        this.control = control;
        this.exceptionFactory = control.getRealConnection().getExceptionFactory();
    }
    
    public final void setAutoCommit(final boolean b) throws SQLException {
        try {
            this.control.checkAutoCommit(b);
            this.getRealConnection().setAutoCommit(b);
            this.stateAutoCommit = b;
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final boolean getAutoCommit() throws SQLException {
        try {
            return this.getRealConnection().getAutoCommit();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final Statement createStatement() throws SQLException {
        try {
            return this.control.wrapStatement(this.getRealConnection().createStatement());
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final PreparedStatement prepareStatement(final String s) throws SQLException {
        try {
            return this.control.wrapStatement(this.getRealConnection().prepareStatement(s), s, null);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final CallableStatement prepareCall(final String s) throws SQLException {
        try {
            return this.control.wrapStatement(this.getRealConnection().prepareCall(s), s);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final String nativeSQL(final String s) throws SQLException {
        try {
            return this.getRealConnection().nativeSQL(s);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final void commit() throws SQLException {
        try {
            this.control.checkCommit();
            this.getRealConnection().commit();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final void rollback() throws SQLException {
        try {
            this.control.checkRollback();
            this.getRealConnection().rollback();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final void close() throws SQLException {
        if (this.isClosed) {
            return;
        }
        try {
            this.control.checkClose();
            if (!this.control.closingConnection()) {
                this.isClosed = true;
                return;
            }
            this.isClosed = true;
            this.getRealConnection().close();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final boolean isClosed() throws SQLException {
        if (this.isClosed) {
            return true;
        }
        try {
            final boolean closed = this.getRealConnection().isClosed();
            if (closed) {
                this.control.closingConnection();
                this.isClosed = true;
            }
            return closed;
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final SQLWarning getWarnings() throws SQLException {
        try {
            return this.getRealConnection().getWarnings();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final void clearWarnings() throws SQLException {
        try {
            this.getRealConnection().clearWarnings();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final DatabaseMetaData getMetaData() throws SQLException {
        try {
            return this.getRealConnection().getMetaData();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final void setReadOnly(final boolean b) throws SQLException {
        try {
            this.getRealConnection().setReadOnly(b);
            this.stateReadOnly = b;
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final boolean isReadOnly() throws SQLException {
        try {
            return this.getRealConnection().isReadOnly();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final void setCatalog(final String catalog) throws SQLException {
        try {
            this.getRealConnection().setCatalog(catalog);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final String getCatalog() throws SQLException {
        try {
            return this.getRealConnection().getCatalog();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final void setTransactionIsolation(final int n) throws SQLException {
        try {
            this.getRealConnection().setTransactionIsolation(n);
            this.stateIsolationLevel = n;
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final int getTransactionIsolation() throws SQLException {
        try {
            return this.getRealConnection().getTransactionIsolation();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final Statement createStatement(final int n, final int n2) throws SQLException {
        try {
            return this.control.wrapStatement(this.getRealConnection().createStatement(n, n2));
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final PreparedStatement prepareStatement(final String s, final int n, final int n2) throws SQLException {
        try {
            return this.control.wrapStatement(this.getRealConnection().prepareStatement(s, n, n2), s, null);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final CallableStatement prepareCall(final String s, final int n, final int n2) throws SQLException {
        try {
            return this.control.wrapStatement(this.getRealConnection().prepareCall(s, n, n2), s);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        try {
            return this.getRealConnection().getTypeMap();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final void setTypeMap(final Map<String, Class<?>> typeMap) throws SQLException {
        try {
            this.getRealConnection().setTypeMap(typeMap);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final Statement createStatement(final int n, final int n2, int statementHoldabilityCheck) throws SQLException {
        try {
            statementHoldabilityCheck = this.statementHoldabilityCheck(statementHoldabilityCheck);
            return this.control.wrapStatement(this.getRealConnection().createStatement(n, n2, statementHoldabilityCheck));
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final CallableStatement prepareCall(final String s, final int n, final int n2, int statementHoldabilityCheck) throws SQLException {
        try {
            statementHoldabilityCheck = this.statementHoldabilityCheck(statementHoldabilityCheck);
            return this.control.wrapStatement(this.getRealConnection().prepareCall(s, n, n2, statementHoldabilityCheck), s);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final Savepoint setSavepoint() throws SQLException {
        try {
            this.control.checkSavepoint();
            return this.getRealConnection().setSavepoint();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final Savepoint setSavepoint(final String savepoint) throws SQLException {
        try {
            this.control.checkSavepoint();
            return this.getRealConnection().setSavepoint(savepoint);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final void rollback(final Savepoint savepoint) throws SQLException {
        try {
            this.control.checkRollback();
            this.getRealConnection().rollback(savepoint);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        try {
            this.getRealConnection().releaseSavepoint(savepoint);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final void setHoldability(int checkHoldCursors) throws SQLException {
        try {
            checkHoldCursors = this.control.checkHoldCursors(checkHoldCursors, false);
            this.getRealConnection().setHoldability(checkHoldCursors);
            this.stateHoldability = checkHoldCursors;
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final PreparedStatement prepareStatement(final String s, final int i) throws SQLException {
        try {
            return this.control.wrapStatement(this.getRealConnection().prepareStatement(s, i), s, i);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final PreparedStatement prepareStatement(final String s, final int[] array) throws SQLException {
        try {
            return this.control.wrapStatement(this.getRealConnection().prepareStatement(s, array), s, array);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final PreparedStatement prepareStatement(final String s, final String[] array) throws SQLException {
        try {
            return this.control.wrapStatement(this.getRealConnection().prepareStatement(s, array), s, array);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    final SQLException noCurrentConnection() {
        return this.exceptionFactory.getSQLException("08003", null, null, null);
    }
    
    final EngineConnection getRealConnection() throws SQLException {
        if (this.isClosed) {
            throw this.noCurrentConnection();
        }
        return this.control.getRealConnection();
    }
    
    final void notifyException(final SQLException ex) {
        if (!this.isClosed) {
            this.control.notifyException(ex);
        }
    }
    
    public void syncState() throws SQLException {
        final EngineConnection realConnection = this.getRealConnection();
        this.stateIsolationLevel = realConnection.getTransactionIsolation();
        this.stateReadOnly = realConnection.isReadOnly();
        this.stateAutoCommit = realConnection.getAutoCommit();
        this.stateHoldability = realConnection.getHoldability();
    }
    
    public void getIsolationUptoDate() throws SQLException {
        if (this.control.isIsolationLevelSetUsingSQLorJDBC()) {
            this.stateIsolationLevel = this.getRealConnection().getTransactionIsolation();
            this.control.resetIsolationLevelFlag();
        }
    }
    
    public void setState(final boolean b) throws SQLException {
        if (b) {
            final EngineConnection realConnection = this.getRealConnection();
            realConnection.setTransactionIsolation(this.stateIsolationLevel);
            realConnection.setReadOnly(this.stateReadOnly);
            realConnection.setAutoCommit(this.stateAutoCommit);
            realConnection.setHoldability(this.stateHoldability);
        }
    }
    
    public BrokeredStatement newBrokeredStatement(final BrokeredStatementControl brokeredStatementControl) throws SQLException {
        return new BrokeredStatement(brokeredStatementControl);
    }
    
    public BrokeredPreparedStatement newBrokeredStatement(final BrokeredStatementControl brokeredStatementControl, final String s, final Object o) throws SQLException {
        return new BrokeredPreparedStatement(brokeredStatementControl, s, o);
    }
    
    public BrokeredCallableStatement newBrokeredStatement(final BrokeredStatementControl brokeredStatementControl, final String s) throws SQLException {
        return new BrokeredCallableStatement(brokeredStatementControl, s);
    }
    
    public final void setDrdaID(final String drdaID) {
        try {
            this.getRealConnection().setDrdaID(drdaID);
        }
        catch (SQLException ex) {}
    }
    
    public boolean isInGlobalTransaction() {
        return this.control.isInGlobalTransaction();
    }
    
    public final void setPrepareIsolation(final int prepareIsolation) throws SQLException {
        this.getRealConnection().setPrepareIsolation(prepareIsolation);
    }
    
    public final int getPrepareIsolation() throws SQLException {
        return this.getRealConnection().getPrepareIsolation();
    }
    
    public final void addWarning(final SQLWarning sqlWarning) throws SQLException {
        this.getRealConnection().addWarning(sqlWarning);
    }
    
    @Override
    public String toString() {
        if (this.connString == null) {
            String string;
            try {
                string = this.getRealConnection().toString();
            }
            catch (SQLException ex) {
                string = "<none>";
            }
            this.connString = this.getClass().getName() + "@" + this.hashCode() + ", Wrapped Connection = " + string;
        }
        return this.connString;
    }
    
    public final PreparedStatement prepareStatement(final String s, final int n, final int n2, int statementHoldabilityCheck) throws SQLException {
        try {
            statementHoldabilityCheck = this.statementHoldabilityCheck(statementHoldabilityCheck);
            return this.control.wrapStatement(this.getRealConnection().prepareStatement(s, n, n2, statementHoldabilityCheck), s, null);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public final int getHoldability() throws SQLException {
        try {
            return this.getRealConnection().getHoldability();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    final int statementHoldabilityCheck(final int n) throws SQLException {
        final int checkHoldCursors = this.control.checkHoldCursors(n, true);
        if (checkHoldCursors != n) {
            this.addWarning(SQLWarningFactory.newSQLWarning("01J07"));
        }
        return checkHoldCursors;
    }
    
    public void clearLOBMapping() throws SQLException {
        this.getRealConnection().clearLOBMapping();
    }
    
    public Object getLOBMapping(final int n) throws SQLException {
        return this.getRealConnection().getLOBMapping(n);
    }
    
    public String getCurrentSchemaName() throws SQLException {
        try {
            return this.getRealConnection().getCurrentSchemaName();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public void resetFromPool() throws SQLException {
        this.getRealConnection().resetFromPool();
    }
    
    public final ExceptionFactory getExceptionFactory() {
        return this.exceptionFactory;
    }
    
    public String getSchema() throws SQLException {
        try {
            return this.getRealConnection().getSchema();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    public void setSchema(final String schema) throws SQLException {
        try {
            this.getRealConnection().setSchema(schema);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
}
