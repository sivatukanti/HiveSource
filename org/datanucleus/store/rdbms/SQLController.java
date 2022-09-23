// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.connection.ManagedConnectionResourceListener;
import java.sql.ResultSet;
import org.datanucleus.ExecutionContext;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.concurrent.ConcurrentHashMap;
import org.datanucleus.store.connection.ManagedConnection;
import java.util.Map;
import org.datanucleus.util.Localiser;

public class SQLController
{
    protected static final Localiser LOCALISER;
    protected boolean supportsBatching;
    protected int maxBatchSize;
    protected int queryTimeout;
    protected boolean jdbcStatements;
    protected boolean paramValuesInBrackets;
    Map<ManagedConnection, ConnectionStatementState> connectionStatements;
    
    public SQLController(final boolean supportsBatching, final int maxBatchSize, final int queryTimeout, final String stmtLogging) {
        this.supportsBatching = false;
        this.maxBatchSize = -1;
        this.queryTimeout = 0;
        this.jdbcStatements = false;
        this.paramValuesInBrackets = true;
        this.connectionStatements = new ConcurrentHashMap<ManagedConnection, ConnectionStatementState>();
        this.supportsBatching = supportsBatching;
        this.maxBatchSize = maxBatchSize;
        this.queryTimeout = queryTimeout;
        if (maxBatchSize == 0) {
            this.supportsBatching = false;
        }
        if (stmtLogging.equalsIgnoreCase("jdbc")) {
            this.jdbcStatements = true;
        }
        else if (stmtLogging.equalsIgnoreCase("values-in-brackets")) {
            this.paramValuesInBrackets = true;
        }
        else {
            this.paramValuesInBrackets = false;
        }
    }
    
    public PreparedStatement getStatementForUpdate(final ManagedConnection conn, final String stmtText, final boolean batchable) throws SQLException {
        return this.getStatementForUpdate(conn, stmtText, batchable, false);
    }
    
    public PreparedStatement getStatementForUpdate(final ManagedConnection conn, final String stmtText, boolean batchable, final boolean getGeneratedKeysFlag) throws SQLException {
        final Connection c = (Connection)conn.getConnection();
        if (this.supportsBatching) {
            final ConnectionStatementState state = this.getConnectionStatementState(conn);
            if (state != null) {
                if (state.processable) {
                    if (!batchable) {
                        this.processConnectionStatement(conn);
                    }
                    else if (state.stmtText.equals(stmtText)) {
                        if (this.maxBatchSize == -1 || state.batchSize < this.maxBatchSize) {
                            final ConnectionStatementState connectionStatementState = state;
                            ++connectionStatementState.batchSize;
                            state.processable = false;
                            if (NucleusLogger.DATASTORE_PERSIST.isDebugEnabled()) {
                                NucleusLogger.DATASTORE_PERSIST.debug(SQLController.LOCALISER.msg("052100", stmtText, "" + state.batchSize));
                            }
                            return state.stmt;
                        }
                        if (NucleusLogger.DATASTORE_PERSIST.isDebugEnabled()) {
                            NucleusLogger.DATASTORE_PERSIST.debug(SQLController.LOCALISER.msg("052101", state.stmtText));
                        }
                        this.processConnectionStatement(conn);
                    }
                    else {
                        this.processConnectionStatement(conn);
                    }
                }
                else if (batchable) {
                    if (NucleusLogger.DATASTORE_PERSIST.isDebugEnabled()) {
                        NucleusLogger.DATASTORE_PERSIST.debug(SQLController.LOCALISER.msg("052102", state.stmtText, stmtText));
                    }
                    batchable = false;
                }
            }
        }
        PreparedStatement ps = getGeneratedKeysFlag ? c.prepareStatement(stmtText, 1) : c.prepareStatement(stmtText);
        ps.clearBatch();
        if (!this.jdbcStatements) {
            ps = new ParamLoggingPreparedStatement(ps, stmtText);
            ((ParamLoggingPreparedStatement)ps).setParamsInAngleBrackets(this.paramValuesInBrackets);
        }
        if (NucleusLogger.DATASTORE.isDebugEnabled()) {
            NucleusLogger.DATASTORE.debug(SQLController.LOCALISER.msg("052109", ps, StringUtils.toJVMIDString(c)));
        }
        if (batchable && this.supportsBatching) {
            if (NucleusLogger.DATASTORE_PERSIST.isDebugEnabled()) {
                NucleusLogger.DATASTORE_PERSIST.debug(SQLController.LOCALISER.msg("052103", stmtText));
            }
            final ConnectionStatementState state2 = new ConnectionStatementState();
            state2.stmt = ps;
            state2.stmtText = stmtText;
            state2.batchSize = 1;
            this.setConnectionStatementState(conn, state2);
        }
        return ps;
    }
    
    public PreparedStatement getStatementForQuery(final ManagedConnection conn, final String stmtText) throws SQLException {
        return this.getStatementForQuery(conn, stmtText, null, null);
    }
    
    public PreparedStatement getStatementForQuery(final ManagedConnection conn, final String stmtText, final String resultSetType, final String resultSetConcurrency) throws SQLException {
        final Connection c = (Connection)conn.getConnection();
        if (this.supportsBatching) {
            final ConnectionStatementState state = this.getConnectionStatementState(conn);
            if (state != null && state.processable) {
                this.processConnectionStatement(conn);
            }
        }
        PreparedStatement ps = null;
        if (resultSetType != null || resultSetConcurrency != null) {
            int rsTypeValue = 1003;
            if (resultSetType != null) {
                if (resultSetType.equals("scroll-sensitive")) {
                    rsTypeValue = 1005;
                }
                else if (resultSetType.equals("scroll-insensitive")) {
                    rsTypeValue = 1004;
                }
            }
            int rsConcurrencyValue = 1007;
            if (resultSetConcurrency != null && resultSetConcurrency.equals("updateable")) {
                rsConcurrencyValue = 1008;
            }
            ps = c.prepareStatement(stmtText, rsTypeValue, rsConcurrencyValue);
            ps.clearBatch();
        }
        else {
            ps = c.prepareStatement(stmtText);
            ps.clearBatch();
        }
        if (this.queryTimeout > 0) {
            ps.setQueryTimeout(this.queryTimeout / 1000);
        }
        if (NucleusLogger.DATASTORE.isDebugEnabled()) {
            NucleusLogger.DATASTORE.debug(SQLController.LOCALISER.msg("052110", StringUtils.toJVMIDString(ps)));
        }
        if (!this.jdbcStatements) {
            ps = new ParamLoggingPreparedStatement(ps, stmtText);
            ((ParamLoggingPreparedStatement)ps).setParamsInAngleBrackets(this.paramValuesInBrackets);
        }
        return ps;
    }
    
    public int[] executeStatementUpdate(final ExecutionContext ec, final ManagedConnection conn, final String stmt, final PreparedStatement ps, final boolean processNow) throws SQLException {
        final ConnectionStatementState state = this.getConnectionStatementState(conn);
        if (state != null) {
            if (state.stmt == ps) {
                if (NucleusLogger.DATASTORE_PERSIST.isDebugEnabled()) {
                    NucleusLogger.DATASTORE_PERSIST.debug(SQLController.LOCALISER.msg("052104", state.stmtText, "" + state.batchSize));
                }
                state.processable = true;
                state.stmt.addBatch();
                if (processNow) {
                    state.closeStatementOnProcess = false;
                    return this.processConnectionStatement(conn);
                }
                return null;
            }
            else {
                this.processConnectionStatement(conn);
            }
        }
        final long startTime = System.currentTimeMillis();
        if (NucleusLogger.DATASTORE_NATIVE.isDebugEnabled()) {
            if (ps instanceof ParamLoggingPreparedStatement) {
                NucleusLogger.DATASTORE_NATIVE.debug(((ParamLoggingPreparedStatement)ps).getStatementWithParamsReplaced());
            }
            else {
                NucleusLogger.DATASTORE_NATIVE.debug(stmt);
            }
        }
        final int ind = ps.executeUpdate();
        if (ec != null && ec.getStatistics() != null) {
            ec.getStatistics().incrementNumWrites();
        }
        ps.clearBatch();
        if (NucleusLogger.DATASTORE_PERSIST.isDebugEnabled()) {
            NucleusLogger.DATASTORE_PERSIST.debug(SQLController.LOCALISER.msg("045001", "" + (System.currentTimeMillis() - startTime), "" + ind, StringUtils.toJVMIDString(ps)));
        }
        return new int[] { ind };
    }
    
    public ResultSet executeStatementQuery(final ExecutionContext ec, final ManagedConnection conn, final String stmt, final PreparedStatement ps) throws SQLException {
        if (this.supportsBatching) {
            final ConnectionStatementState state = this.getConnectionStatementState(conn);
            if (state != null) {
                if (state.processable) {
                    this.processConnectionStatement(conn);
                }
                else if (NucleusLogger.DATASTORE_RETRIEVE.isDebugEnabled()) {
                    NucleusLogger.DATASTORE_RETRIEVE.debug(SQLController.LOCALISER.msg("052106", state.stmtText, stmt));
                }
            }
        }
        final long startTime = System.currentTimeMillis();
        if (NucleusLogger.DATASTORE_NATIVE.isDebugEnabled()) {
            if (ps instanceof ParamLoggingPreparedStatement) {
                NucleusLogger.DATASTORE_NATIVE.debug(((ParamLoggingPreparedStatement)ps).getStatementWithParamsReplaced());
            }
            else {
                NucleusLogger.DATASTORE_NATIVE.debug(stmt);
            }
        }
        final ResultSet rs = ps.executeQuery();
        if (ec != null && ec.getStatistics() != null) {
            ec.getStatistics().incrementNumReads();
        }
        ps.clearBatch();
        if (NucleusLogger.DATASTORE_RETRIEVE.isDebugEnabled()) {
            NucleusLogger.DATASTORE_RETRIEVE.debug(SQLController.LOCALISER.msg("045000", System.currentTimeMillis() - startTime));
        }
        return rs;
    }
    
    public void abortStatementForConnection(final ManagedConnection conn, final PreparedStatement ps) {
        final ConnectionStatementState state = this.getConnectionStatementState(conn);
        if (state != null && state.stmt == ps) {
            try {
                this.removeConnectionStatementState(conn);
                ps.close();
            }
            catch (SQLException ex) {}
        }
    }
    
    public void closeStatement(final ManagedConnection conn, final PreparedStatement ps) throws SQLException {
        final ConnectionStatementState state = this.getConnectionStatementState(conn);
        if (state != null && state.stmt == ps) {
            state.closeStatementOnProcess = true;
        }
        else {
            try {
                if (NucleusLogger.DATASTORE.isDebugEnabled()) {
                    NucleusLogger.DATASTORE.debug(SQLController.LOCALISER.msg("052110", StringUtils.toJVMIDString(ps)));
                }
                ps.close();
            }
            catch (SQLException sqle) {
                if (!sqle.getMessage().equals("Already closed")) {
                    throw sqle;
                }
            }
        }
    }
    
    public void processStatementsForConnection(final ManagedConnection conn) throws SQLException {
        if (!this.supportsBatching || this.getConnectionStatementState(conn) == null) {
            return;
        }
        this.processConnectionStatement(conn);
    }
    
    protected int[] processConnectionStatement(final ManagedConnection conn) throws SQLException {
        final ConnectionStatementState state = this.getConnectionStatementState(conn);
        if (state == null || !state.processable) {
            return null;
        }
        final long startTime = System.currentTimeMillis();
        if (NucleusLogger.DATASTORE_NATIVE.isDebugEnabled()) {
            if (state.stmt instanceof ParamLoggingPreparedStatement) {
                NucleusLogger.DATASTORE_NATIVE.debug(((ParamLoggingPreparedStatement)state.stmt).getStatementWithParamsReplaced());
            }
            else {
                NucleusLogger.DATASTORE_NATIVE.debug(state.stmtText);
            }
        }
        final int[] ind = state.stmt.executeBatch();
        state.stmt.clearBatch();
        if (NucleusLogger.DATASTORE.isDebugEnabled()) {
            NucleusLogger.DATASTORE.debug(SQLController.LOCALISER.msg("045001", "" + (System.currentTimeMillis() - startTime), StringUtils.intArrayToString(ind), StringUtils.toJVMIDString(state.stmt)));
        }
        this.removeConnectionStatementState(conn);
        if (state.closeStatementOnProcess) {
            state.stmt.close();
        }
        return ind;
    }
    
    protected void removeConnectionStatementState(final ManagedConnection conn) {
        this.connectionStatements.remove(conn);
    }
    
    protected ConnectionStatementState getConnectionStatementState(final ManagedConnection conn) {
        return this.connectionStatements.get(conn);
    }
    
    protected void setConnectionStatementState(final ManagedConnection conn, final ConnectionStatementState state) {
        this.connectionStatements.put(conn, state);
        conn.addListener(new ManagedConnectionResourceListener() {
            @Override
            public void transactionFlushed() {
                try {
                    SQLController.this.processStatementsForConnection(conn);
                }
                catch (SQLException e) {
                    final ConnectionStatementState state = SQLController.this.getConnectionStatementState(conn);
                    if (state != null) {
                        SQLController.this.removeConnectionStatementState(conn);
                        if (state.closeStatementOnProcess) {
                            try {
                                state.stmt.close();
                            }
                            catch (SQLException ex) {}
                        }
                    }
                    throw new NucleusDataStoreException(SQLController.LOCALISER.msg("052108"), e);
                }
            }
            
            @Override
            public void transactionPreClose() {
            }
            
            @Override
            public void managedConnectionPreClose() {
            }
            
            @Override
            public void managedConnectionPostClose() {
            }
            
            @Override
            public void resourcePostClose() {
            }
        });
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
    
    static class ConnectionStatementState
    {
        PreparedStatement stmt;
        String stmtText;
        int batchSize;
        boolean processable;
        boolean closeStatementOnProcess;
        
        ConnectionStatementState() {
            this.stmt = null;
            this.stmtText = null;
            this.batchSize = 0;
            this.processable = false;
            this.closeStatementOnProcess = false;
        }
        
        @Override
        public String toString() {
            return "StmtState : stmt=" + StringUtils.toJVMIDString(this.stmt) + " sql=" + this.stmtText + " batch=" + this.batchSize + " closeOnProcess=" + this.closeStatementOnProcess;
        }
    }
}
