// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import org.slf4j.LoggerFactory;
import com.google.common.base.Objects;
import java.lang.reflect.Proxy;
import java.sql.Savepoint;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.SQLWarning;
import java.sql.DatabaseMetaData;
import java.util.concurrent.Executor;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Array;
import java.sql.Struct;
import java.sql.SQLClientInfoException;
import java.util.Properties;
import java.util.Iterator;
import java.net.SocketException;
import com.jolbox.bonecp.hooks.ConnectionState;
import java.util.ArrayList;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import com.google.common.collect.MapMaker;
import java.sql.Statement;
import java.util.concurrent.ConcurrentMap;
import com.google.common.collect.ImmutableSet;
import java.lang.ref.Reference;
import java.util.Map;
import com.google.common.annotations.VisibleForTesting;
import com.jolbox.bonecp.proxy.TransactionRecoveryResult;
import java.util.List;
import com.jolbox.bonecp.hooks.ConnectionHook;
import org.slf4j.Logger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.Serializable;
import java.sql.Connection;

public class ConnectionHandle implements Connection, Serializable
{
    private static final long serialVersionUID = 5969210523116801522L;
    private static final String SET_AUTO_COMMIT_FALSE_WAS_CALLED_MESSAGE = "setAutoCommit(false) was called but transaction was not COMMITted or ROLLBACKed properly before it was closed.\n";
    private static final String STATEMENT_NOT_CLOSED = "Stack trace of location where statement was opened follows:\n%s";
    private static final String LOG_ERROR_MESSAGE = "Connection closed twice exception detected.\n%s\n%s\n";
    private static final String UNCLOSED_LOG_ERROR_MESSAGE = "Statement was not properly closed off before this connection was closed.\n%s";
    private static final String CLOSED_TWICE_EXCEPTION_MESSAGE = "Connection closed from thread [%s] was closed again.\nStack trace of location where connection was first closed follows:\n";
    protected static boolean testSupport;
    protected Connection connection;
    private long connectionLastUsedInMs;
    private long connectionLastResetInMs;
    protected long connectionCreationTimeInMs;
    private BoneCP pool;
    private Boolean defaultReadOnly;
    private String defaultCatalog;
    private int defaultTransactionIsolationValue;
    private Boolean defaultAutoCommit;
    protected boolean resetConnectionOnClose;
    protected boolean possiblyBroken;
    protected AtomicBoolean logicallyClosed;
    private ConnectionPartition originatingPartition;
    private IStatementCache preparedStatementCache;
    private IStatementCache callableStatementCache;
    protected static Logger logger;
    private Object debugHandle;
    private ConnectionHook connectionHook;
    protected boolean doubleCloseCheck;
    protected volatile String doubleCloseException;
    private boolean logStatementsEnabled;
    protected boolean statementCachingEnabled;
    private List<ReplayLog> replayLog;
    private boolean inReplayMode;
    protected TransactionRecoveryResult recoveryResult;
    protected String url;
    protected Thread threadUsingConnection;
    @VisibleForTesting
    protected long maxConnectionAgeInMs;
    private boolean statisticsEnabled;
    private Statistics statistics;
    private volatile Thread threadWatch;
    protected Map<Connection, Reference<ConnectionHandle>> finalizableRefs;
    protected boolean connectionTrackingDisabled;
    @VisibleForTesting
    protected boolean txResolved;
    @VisibleForTesting
    protected boolean detectUnresolvedTransactions;
    protected String autoCommitStackTrace;
    protected boolean detectUnclosedStatements;
    protected boolean closeOpenStatements;
    private static final ImmutableSet<String> sqlStateDBFailureCodes;
    protected ConcurrentMap<Statement, String> trackedStatement;
    private final String noStackTrace = "";
    
    protected ConnectionHandle(final Connection connection, final ConnectionPartition partition, final BoneCP pool, final boolean recreating) throws SQLException {
        this.connection = null;
        this.defaultTransactionIsolationValue = -1;
        this.logicallyClosed = new AtomicBoolean();
        this.originatingPartition = null;
        this.preparedStatementCache = null;
        this.callableStatementCache = null;
        this.doubleCloseException = null;
        this.txResolved = true;
        final boolean newConnection = connection == null;
        this.originatingPartition = partition;
        this.pool = pool;
        this.connectionHook = pool.getConfig().getConnectionHook();
        if (!recreating) {
            this.connectionLastUsedInMs = System.currentTimeMillis();
            this.connectionLastResetInMs = System.currentTimeMillis();
            this.connectionCreationTimeInMs = System.currentTimeMillis();
        }
        this.url = pool.getConfig().getJdbcUrl();
        this.finalizableRefs = pool.getFinalizableRefs();
        this.defaultReadOnly = pool.getConfig().getDefaultReadOnly();
        this.defaultCatalog = pool.getConfig().getDefaultCatalog();
        this.defaultTransactionIsolationValue = pool.getConfig().getDefaultTransactionIsolationValue();
        this.defaultAutoCommit = pool.getConfig().getDefaultAutoCommit();
        this.resetConnectionOnClose = pool.getConfig().isResetConnectionOnClose();
        this.connectionTrackingDisabled = pool.getConfig().isDisableConnectionTracking();
        this.statisticsEnabled = pool.getConfig().isStatisticsEnabled();
        this.statistics = pool.getStatistics();
        this.detectUnresolvedTransactions = pool.getConfig().isDetectUnresolvedTransactions();
        this.detectUnclosedStatements = pool.getConfig().isDetectUnclosedStatements();
        this.closeOpenStatements = pool.getConfig().isCloseOpenStatements();
        if (this.closeOpenStatements) {
            this.trackedStatement = new MapMaker().makeMap();
        }
        this.threadUsingConnection = null;
        this.connectionHook = this.pool.getConfig().getConnectionHook();
        this.maxConnectionAgeInMs = pool.getConfig().getMaxConnectionAge(TimeUnit.MILLISECONDS);
        this.doubleCloseCheck = pool.getConfig().isCloseConnectionWatch();
        this.logStatementsEnabled = pool.getConfig().isLogStatementsEnabled();
        final int cacheSize = pool.getConfig().getStatementsCacheSize();
        if (cacheSize > 0 && newConnection) {
            this.preparedStatementCache = new StatementCache(cacheSize, pool.getConfig().isStatisticsEnabled(), pool.getStatistics());
            this.callableStatementCache = new StatementCache(cacheSize, pool.getConfig().isStatisticsEnabled(), pool.getStatistics());
            this.statementCachingEnabled = true;
        }
        try {
            this.connection = (newConnection ? pool.obtainInternalConnection(this) : connection);
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        if (this.pool.getConfig().isTransactionRecoveryEnabled()) {
            this.replayLog = new ArrayList<ReplayLog>(30);
            this.recoveryResult = new TransactionRecoveryResult();
            if (!recreating) {
                this.connection = MemorizeTransactionProxy.memorize(this.connection, this);
            }
        }
        if (!newConnection && !connection.getAutoCommit() && !connection.isClosed()) {
            connection.rollback();
        }
        if (this.defaultAutoCommit != null) {
            this.setAutoCommit(this.defaultAutoCommit);
        }
        if (this.defaultReadOnly != null) {
            this.setReadOnly(this.defaultReadOnly);
        }
        if (this.defaultCatalog != null) {
            this.setCatalog(this.defaultCatalog);
        }
        if (this.defaultTransactionIsolationValue != -1) {
            this.setTransactionIsolation(this.defaultTransactionIsolationValue);
        }
    }
    
    public ConnectionHandle recreateConnectionHandle() throws SQLException {
        final ConnectionHandle handle = new ConnectionHandle(this.connection, this.originatingPartition, this.pool, true);
        handle.originatingPartition = this.originatingPartition;
        handle.connectionCreationTimeInMs = this.connectionCreationTimeInMs;
        handle.connectionLastResetInMs = this.connectionLastResetInMs;
        handle.connectionLastUsedInMs = this.connectionLastUsedInMs;
        handle.preparedStatementCache = this.preparedStatementCache;
        handle.callableStatementCache = this.callableStatementCache;
        handle.statementCachingEnabled = this.statementCachingEnabled;
        handle.connectionHook = this.connectionHook;
        handle.possiblyBroken = this.possiblyBroken;
        handle.debugHandle = this.debugHandle;
        this.connection = null;
        return handle;
    }
    
    protected static ConnectionHandle createTestConnectionHandle(final Connection connection, final IStatementCache preparedStatementCache, final IStatementCache callableStatementCache, final BoneCP pool) {
        final ConnectionHandle handle = new ConnectionHandle();
        handle.connection = connection;
        handle.preparedStatementCache = preparedStatementCache;
        handle.callableStatementCache = callableStatementCache;
        handle.connectionLastUsedInMs = System.currentTimeMillis();
        handle.connectionLastResetInMs = System.currentTimeMillis();
        handle.connectionCreationTimeInMs = System.currentTimeMillis();
        handle.recoveryResult = new TransactionRecoveryResult();
        handle.trackedStatement = new MapMaker().makeMap();
        handle.url = "foo";
        handle.closeOpenStatements = true;
        handle.pool = pool;
        handle.url = null;
        final int cacheSize = pool.getConfig().getStatementsCacheSize();
        if (cacheSize > 0) {
            handle.statementCachingEnabled = true;
        }
        return handle;
    }
    
    private ConnectionHandle() {
        this.connection = null;
        this.defaultTransactionIsolationValue = -1;
        this.logicallyClosed = new AtomicBoolean();
        this.originatingPartition = null;
        this.preparedStatementCache = null;
        this.callableStatementCache = null;
        this.doubleCloseException = null;
        this.txResolved = true;
    }
    
    public void sendInitSQL() throws SQLException {
        sendInitSQL(this.connection, this.pool.getConfig().getInitSQL());
    }
    
    protected static void sendInitSQL(final Connection connection, final String initSQL) throws SQLException {
        if (initSQL != null) {
            Statement stmt = null;
            try {
                stmt = connection.createStatement();
                stmt.execute(initSQL);
                if (ConnectionHandle.testSupport) {
                    stmt = null;
                }
            }
            finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        }
    }
    
    protected SQLException markPossiblyBroken(final SQLException e) {
        String state = e.getSQLState();
        boolean alreadyDestroyed = false;
        final ConnectionState connectionState = (this.getConnectionHook() != null) ? this.getConnectionHook().onMarkPossiblyBroken(this, state, e) : ConnectionState.NOP;
        if (state == null) {
            state = "08999";
        }
        if ((ConnectionHandle.sqlStateDBFailureCodes.contains(state) || connectionState.equals(ConnectionState.TERMINATE_ALL_CONNECTIONS)) && this.pool != null && this.pool.getDbIsDown().compareAndSet(false, true)) {
            ConnectionHandle.logger.error("Database access problem. Killing off this connection and all remaining connections in the connection pool. SQL State = " + state);
            this.pool.connectionStrategy.terminateAllConnections();
            this.pool.destroyConnection(this);
            this.logicallyClosed.set(true);
            alreadyDestroyed = true;
            for (int i = 0; i < this.pool.partitionCount; ++i) {
                this.pool.partitions[i].getPoolWatchThreadSignalQueue().offer(new Object());
            }
        }
        if ((state.equals("08003") || ConnectionHandle.sqlStateDBFailureCodes.contains(state) || e.getCause() instanceof SocketException) && !alreadyDestroyed) {
            this.pool.destroyConnection(this);
            this.logicallyClosed.set(true);
            this.getOriginatingPartition().getPoolWatchThreadSignalQueue().offer(new Object());
        }
        final char firstChar = state.charAt(0);
        if (connectionState.equals(ConnectionState.CONNECTION_POSSIBLY_BROKEN) || state.equals("40001") || state.startsWith("08") || (firstChar >= '5' && firstChar <= '9')) {
            this.possiblyBroken = true;
        }
        if (this.possiblyBroken && this.getConnectionHook() != null) {
            this.possiblyBroken = this.getConnectionHook().onConnectionException(this, state, e);
        }
        return e;
    }
    
    public void clearWarnings() throws SQLException {
        this.checkClosed();
        try {
            this.connection.clearWarnings();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
    }
    
    private void checkClosed() throws SQLException {
        if (this.logicallyClosed.get()) {
            throw new SQLException("Connection is closed!");
        }
    }
    
    public void close() throws SQLException {
        try {
            if (this.resetConnectionOnClose) {
                this.rollback();
                if (!this.getAutoCommit()) {
                    this.setAutoCommit(true);
                }
            }
            if (this.logicallyClosed.compareAndSet(false, true)) {
                if (this.threadWatch != null) {
                    this.threadWatch.interrupt();
                    this.threadWatch = null;
                }
                if (this.closeOpenStatements) {
                    for (final Map.Entry<Statement, String> statementEntry : this.trackedStatement.entrySet()) {
                        statementEntry.getKey().close();
                        if (this.detectUnclosedStatements) {
                            ConnectionHandle.logger.warn(String.format("Statement was not properly closed off before this connection was closed.\n%s", statementEntry.getValue()));
                        }
                    }
                    this.trackedStatement.clear();
                }
                if (!this.connectionTrackingDisabled) {
                    this.pool.getFinalizableRefs().remove(this.connection);
                }
                ConnectionHandle handle = null;
                try {
                    handle = this.recreateConnectionHandle();
                    this.pool.connectionStrategy.cleanupConnection(this, handle);
                    this.pool.releaseConnection(handle);
                }
                catch (SQLException e) {
                    if (!this.isClosed()) {
                        this.pool.connectionStrategy.cleanupConnection(this, handle);
                        this.pool.releaseConnection(this);
                    }
                    throw e;
                }
                if (this.doubleCloseCheck) {
                    this.doubleCloseException = this.pool.captureStackTrace("Connection closed from thread [%s] was closed again.\nStack trace of location where connection was first closed follows:\n");
                }
            }
            else if (this.doubleCloseCheck && this.doubleCloseException != null) {
                final String currentLocation = this.pool.captureStackTrace("Last closed trace from thread [" + Thread.currentThread().getName() + "]:\n");
                ConnectionHandle.logger.error(String.format("Connection closed twice exception detected.\n%s\n%s\n", this.doubleCloseException, currentLocation));
            }
        }
        catch (SQLException e2) {
            throw this.markPossiblyBroken(e2);
        }
    }
    
    protected void internalClose() throws SQLException {
        try {
            this.clearStatementCaches(true);
            if (this.connection != null) {
                this.connection.close();
                if (!this.connectionTrackingDisabled && this.finalizableRefs != null) {
                    this.finalizableRefs.remove(this.connection);
                }
            }
            this.logicallyClosed.set(true);
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
    }
    
    public void commit() throws SQLException {
        this.checkClosed();
        try {
            this.connection.commit();
            this.txResolved = true;
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
    }
    
    public Properties getClientInfo() throws SQLException {
        Properties result = null;
        this.checkClosed();
        try {
            result = this.connection.getClientInfo();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public String getClientInfo(final String name) throws SQLException {
        String result = null;
        this.checkClosed();
        try {
            result = this.connection.getClientInfo(name);
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public boolean isValid(final int timeout) throws SQLException {
        boolean result = false;
        this.checkClosed();
        try {
            result = this.connection.isValid(timeout);
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return this.connection.isWrapperFor(iface);
    }
    
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return this.connection.unwrap(iface);
    }
    
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        this.connection.setClientInfo(properties);
    }
    
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        this.connection.setClientInfo(name, value);
    }
    
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        Struct result = null;
        this.checkClosed();
        try {
            result = this.connection.createStruct(typeName, attributes);
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        Array result = null;
        this.checkClosed();
        try {
            result = this.connection.createArrayOf(typeName, elements);
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public Blob createBlob() throws SQLException {
        Blob result = null;
        this.checkClosed();
        try {
            result = this.connection.createBlob();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public Clob createClob() throws SQLException {
        Clob result = null;
        this.checkClosed();
        try {
            result = this.connection.createClob();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public NClob createNClob() throws SQLException {
        NClob result = null;
        this.checkClosed();
        try {
            result = this.connection.createNClob();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public SQLXML createSQLXML() throws SQLException {
        SQLXML result = null;
        this.checkClosed();
        try {
            result = this.connection.createSQLXML();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public void setSchema(final String schema) throws SQLException {
        this.connection.setSchema(schema);
    }
    
    public String getSchema() throws SQLException {
        return this.connection.getSchema();
    }
    
    public void abort(final Executor executor) throws SQLException {
        this.connection.abort(executor);
    }
    
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        this.connection.setNetworkTimeout(executor, milliseconds);
    }
    
    public int getNetworkTimeout() throws SQLException {
        return this.connection.getNetworkTimeout();
    }
    
    public Statement createStatement() throws SQLException {
        Statement result = null;
        this.checkClosed();
        try {
            result = new StatementHandle(this.connection.createStatement(), this, this.logStatementsEnabled);
            if (this.closeOpenStatements) {
                this.trackedStatement.put(result, this.maybeCaptureStackTrace());
            }
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        Statement result = null;
        this.checkClosed();
        try {
            result = new StatementHandle(this.connection.createStatement(resultSetType, resultSetConcurrency), this, this.logStatementsEnabled);
            if (this.closeOpenStatements) {
                this.trackedStatement.put(result, this.maybeCaptureStackTrace());
            }
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        Statement result = null;
        this.checkClosed();
        try {
            result = new StatementHandle(this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability), this, this.logStatementsEnabled);
            if (this.closeOpenStatements) {
                this.trackedStatement.put(result, this.maybeCaptureStackTrace());
            }
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    protected String maybeCaptureStackTrace() {
        if (this.detectUnclosedStatements) {
            return this.pool.captureStackTrace("Stack trace of location where statement was opened follows:\n%s");
        }
        this.getClass();
        return "";
    }
    
    public boolean getAutoCommit() throws SQLException {
        boolean result = false;
        this.checkClosed();
        try {
            result = this.connection.getAutoCommit();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public String getCatalog() throws SQLException {
        String result = null;
        this.checkClosed();
        try {
            result = this.connection.getCatalog();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int getHoldability() throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            result = this.connection.getHoldability();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public DatabaseMetaData getMetaData() throws SQLException {
        DatabaseMetaData result = null;
        this.checkClosed();
        try {
            result = this.connection.getMetaData();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int getTransactionIsolation() throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            result = this.connection.getTransactionIsolation();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        Map<String, Class<?>> result = null;
        this.checkClosed();
        try {
            result = this.connection.getTypeMap();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public SQLWarning getWarnings() throws SQLException {
        SQLWarning result = null;
        this.checkClosed();
        try {
            result = this.connection.getWarnings();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public boolean isClosed() {
        return this.logicallyClosed.get();
    }
    
    public boolean isReadOnly() throws SQLException {
        boolean result = false;
        this.checkClosed();
        try {
            result = this.connection.isReadOnly();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public String nativeSQL(final String sql) throws SQLException {
        String result = null;
        this.checkClosed();
        try {
            result = this.connection.nativeSQL(sql);
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public CallableStatement prepareCall(final String sql) throws SQLException {
        StatementHandle result = null;
        String cacheKey = null;
        this.checkClosed();
        try {
            long statStart = 0L;
            if (this.statisticsEnabled) {
                statStart = System.nanoTime();
            }
            if (this.statementCachingEnabled) {
                cacheKey = sql;
                result = this.callableStatementCache.get(cacheKey);
            }
            if (result == null) {
                result = new CallableStatementHandle(this.connection.prepareCall(sql), sql, this, cacheKey, this.callableStatementCache);
                result.setLogicallyOpen();
            }
            if (this.pool.closeConnectionWatch && this.statementCachingEnabled) {
                result.setOpenStackTrace(this.pool.captureStackTrace("Stack trace of location where statement was opened follows:\n%s"));
            }
            if (this.closeOpenStatements) {
                this.trackedStatement.put(result, this.maybeCaptureStackTrace());
            }
            if (this.statisticsEnabled) {
                this.statistics.addStatementPrepareTime(System.nanoTime() - statStart);
                this.statistics.incrementStatementsPrepared();
            }
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return (CallableStatement)result;
    }
    
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        StatementHandle result = null;
        String cacheKey = null;
        this.checkClosed();
        try {
            long statStart = 0L;
            if (this.statisticsEnabled) {
                statStart = System.nanoTime();
            }
            if (this.statementCachingEnabled) {
                cacheKey = this.callableStatementCache.calculateCacheKey(sql, resultSetType, resultSetConcurrency);
                result = this.callableStatementCache.get(cacheKey);
            }
            if (result == null) {
                result = new CallableStatementHandle(this.connection.prepareCall(sql, resultSetType, resultSetConcurrency), sql, this, cacheKey, this.callableStatementCache);
                result.setLogicallyOpen();
            }
            if (this.pool.closeConnectionWatch && this.statementCachingEnabled) {
                result.setOpenStackTrace(this.pool.captureStackTrace("Stack trace of location where statement was opened follows:\n%s"));
            }
            if (this.closeOpenStatements) {
                this.trackedStatement.put(result, this.maybeCaptureStackTrace());
            }
            if (this.statisticsEnabled) {
                this.statistics.addStatementPrepareTime(System.nanoTime() - statStart);
                this.statistics.incrementStatementsPrepared();
            }
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return (CallableStatement)result;
    }
    
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        StatementHandle result = null;
        String cacheKey = null;
        this.checkClosed();
        try {
            long statStart = 0L;
            if (this.statisticsEnabled) {
                statStart = System.nanoTime();
            }
            if (this.statementCachingEnabled) {
                cacheKey = this.callableStatementCache.calculateCacheKey(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
                result = this.callableStatementCache.get(cacheKey);
            }
            if (result == null) {
                result = new CallableStatementHandle(this.connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql, this, cacheKey, this.callableStatementCache);
                result.setLogicallyOpen();
            }
            if (this.pool.closeConnectionWatch && this.statementCachingEnabled) {
                result.setOpenStackTrace(this.pool.captureStackTrace("Stack trace of location where statement was opened follows:\n%s"));
            }
            if (this.closeOpenStatements) {
                this.trackedStatement.put(result, this.maybeCaptureStackTrace());
            }
            if (this.statisticsEnabled) {
                this.statistics.addStatementPrepareTime(System.nanoTime() - statStart);
                this.statistics.incrementStatementsPrepared();
            }
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return (CallableStatement)result;
    }
    
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        StatementHandle result = null;
        String cacheKey = null;
        this.checkClosed();
        try {
            long statStart = 0L;
            if (this.statisticsEnabled) {
                statStart = System.nanoTime();
            }
            if (this.statementCachingEnabled) {
                cacheKey = sql;
                result = this.preparedStatementCache.get(cacheKey);
            }
            if (result == null) {
                result = new PreparedStatementHandle(this.connection.prepareStatement(sql), sql, this, cacheKey, this.preparedStatementCache);
                result.setLogicallyOpen();
            }
            if (this.pool.closeConnectionWatch && this.statementCachingEnabled) {
                result.setOpenStackTrace(this.pool.captureStackTrace("Stack trace of location where statement was opened follows:\n%s"));
            }
            if (this.closeOpenStatements) {
                this.trackedStatement.put(result, this.maybeCaptureStackTrace());
            }
            if (this.statisticsEnabled) {
                this.statistics.addStatementPrepareTime(System.nanoTime() - statStart);
                this.statistics.incrementStatementsPrepared();
            }
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return (PreparedStatement)result;
    }
    
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        StatementHandle result = null;
        String cacheKey = null;
        this.checkClosed();
        try {
            long statStart = 0L;
            if (this.statisticsEnabled) {
                statStart = System.nanoTime();
            }
            if (this.statementCachingEnabled) {
                cacheKey = this.preparedStatementCache.calculateCacheKey(sql, autoGeneratedKeys);
                result = this.preparedStatementCache.get(cacheKey);
            }
            if (result == null) {
                result = new PreparedStatementHandle(this.connection.prepareStatement(sql, autoGeneratedKeys), sql, this, cacheKey, this.preparedStatementCache);
                result.setLogicallyOpen();
            }
            if (this.pool.closeConnectionWatch && this.statementCachingEnabled) {
                result.setOpenStackTrace(this.pool.captureStackTrace("Stack trace of location where statement was opened follows:\n%s"));
            }
            if (this.closeOpenStatements) {
                this.trackedStatement.put(result, this.maybeCaptureStackTrace());
            }
            if (this.statisticsEnabled) {
                this.statistics.addStatementPrepareTime(System.nanoTime() - statStart);
                this.statistics.incrementStatementsPrepared();
            }
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return (PreparedStatement)result;
    }
    
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        StatementHandle result = null;
        String cacheKey = null;
        this.checkClosed();
        try {
            long statStart = 0L;
            if (this.statisticsEnabled) {
                statStart = System.nanoTime();
            }
            if (this.statementCachingEnabled) {
                cacheKey = this.preparedStatementCache.calculateCacheKey(sql, columnIndexes);
                result = this.preparedStatementCache.get(cacheKey);
            }
            if (result == null) {
                result = new PreparedStatementHandle(this.connection.prepareStatement(sql, columnIndexes), sql, this, cacheKey, this.preparedStatementCache);
                result.setLogicallyOpen();
            }
            if (this.pool.closeConnectionWatch && this.statementCachingEnabled) {
                result.setOpenStackTrace(this.pool.captureStackTrace("Stack trace of location where statement was opened follows:\n%s"));
            }
            if (this.closeOpenStatements) {
                this.trackedStatement.put(result, this.maybeCaptureStackTrace());
            }
            if (this.statisticsEnabled) {
                this.statistics.addStatementPrepareTime(System.nanoTime() - statStart);
                this.statistics.incrementStatementsPrepared();
            }
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return (PreparedStatement)result;
    }
    
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        StatementHandle result = null;
        String cacheKey = null;
        this.checkClosed();
        try {
            long statStart = 0L;
            if (this.statisticsEnabled) {
                statStart = System.nanoTime();
            }
            if (this.statementCachingEnabled) {
                cacheKey = this.preparedStatementCache.calculateCacheKey(sql, columnNames);
                result = this.preparedStatementCache.get(cacheKey);
            }
            if (result == null) {
                result = new PreparedStatementHandle(this.connection.prepareStatement(sql, columnNames), sql, this, cacheKey, this.preparedStatementCache);
                result.setLogicallyOpen();
            }
            if (this.pool.closeConnectionWatch && this.statementCachingEnabled) {
                result.setOpenStackTrace(this.pool.captureStackTrace("Stack trace of location where statement was opened follows:\n%s"));
            }
            if (this.closeOpenStatements) {
                this.trackedStatement.put(result, this.maybeCaptureStackTrace());
            }
            if (this.statisticsEnabled) {
                this.statistics.addStatementPrepareTime(System.nanoTime() - statStart);
                this.statistics.incrementStatementsPrepared();
            }
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return (PreparedStatement)result;
    }
    
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        StatementHandle result = null;
        String cacheKey = null;
        this.checkClosed();
        try {
            long statStart = 0L;
            if (this.statisticsEnabled) {
                statStart = System.nanoTime();
            }
            if (this.statementCachingEnabled) {
                cacheKey = this.preparedStatementCache.calculateCacheKey(sql, resultSetType, resultSetConcurrency);
                result = this.preparedStatementCache.get(cacheKey);
            }
            if (result == null) {
                result = new PreparedStatementHandle(this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency), sql, this, cacheKey, this.preparedStatementCache);
                result.setLogicallyOpen();
            }
            if (this.pool.closeConnectionWatch && this.statementCachingEnabled) {
                result.setOpenStackTrace(this.pool.captureStackTrace("Stack trace of location where statement was opened follows:\n%s"));
            }
            if (this.closeOpenStatements) {
                this.trackedStatement.put(result, this.maybeCaptureStackTrace());
            }
            if (this.statisticsEnabled) {
                this.statistics.addStatementPrepareTime(System.nanoTime() - statStart);
                this.statistics.incrementStatementsPrepared();
            }
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return (PreparedStatement)result;
    }
    
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        StatementHandle result = null;
        String cacheKey = null;
        this.checkClosed();
        try {
            long statStart = 0L;
            if (this.statisticsEnabled) {
                statStart = System.nanoTime();
            }
            if (this.statementCachingEnabled) {
                cacheKey = this.preparedStatementCache.calculateCacheKey(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
                result = this.preparedStatementCache.get(cacheKey);
            }
            if (result == null) {
                result = new PreparedStatementHandle(this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql, this, cacheKey, this.preparedStatementCache);
                result.setLogicallyOpen();
            }
            if (this.pool.closeConnectionWatch && this.statementCachingEnabled) {
                result.setOpenStackTrace(this.pool.captureStackTrace("Stack trace of location where statement was opened follows:\n%s"));
            }
            if (this.closeOpenStatements) {
                this.trackedStatement.put(result, this.maybeCaptureStackTrace());
            }
            if (this.statisticsEnabled) {
                this.statistics.addStatementPrepareTime(System.nanoTime() - statStart);
                this.statistics.incrementStatementsPrepared();
            }
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return (PreparedStatement)result;
    }
    
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        this.checkClosed();
        try {
            this.connection.releaseSavepoint(savepoint);
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
    }
    
    public void rollback() throws SQLException {
        this.checkClosed();
        try {
            this.connection.rollback();
            this.txResolved = true;
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
    }
    
    public void rollback(final Savepoint savepoint) throws SQLException {
        this.checkClosed();
        try {
            this.connection.rollback(savepoint);
            this.txResolved = true;
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
    }
    
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        this.checkClosed();
        try {
            this.connection.setAutoCommit(autoCommit);
            this.txResolved = autoCommit;
            if (this.detectUnresolvedTransactions && !autoCommit) {
                this.autoCommitStackTrace = this.pool.captureStackTrace("setAutoCommit(false) was called but transaction was not COMMITted or ROLLBACKed properly before it was closed.\n");
            }
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
    }
    
    public void setCatalog(final String catalog) throws SQLException {
        this.checkClosed();
        try {
            this.connection.setCatalog(catalog);
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
    }
    
    public void setHoldability(final int holdability) throws SQLException {
        this.checkClosed();
        try {
            this.connection.setHoldability(holdability);
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
    }
    
    public void setReadOnly(final boolean readOnly) throws SQLException {
        this.checkClosed();
        try {
            this.connection.setReadOnly(readOnly);
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
    }
    
    public Savepoint setSavepoint() throws SQLException {
        this.checkClosed();
        Savepoint result = null;
        try {
            result = this.connection.setSavepoint();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public Savepoint setSavepoint(final String name) throws SQLException {
        this.checkClosed();
        Savepoint result = null;
        try {
            result = this.connection.setSavepoint(name);
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
        return result;
    }
    
    public void setTransactionIsolation(final int level) throws SQLException {
        this.checkClosed();
        try {
            this.connection.setTransactionIsolation(level);
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
    }
    
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        this.checkClosed();
        try {
            this.connection.setTypeMap(map);
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
    }
    
    public long getConnectionLastUsedInMs() {
        return this.connectionLastUsedInMs;
    }
    
    @Deprecated
    public long getConnectionLastUsed() {
        return this.getConnectionLastUsedInMs();
    }
    
    protected void setConnectionLastUsedInMs(final long connectionLastUsed) {
        this.connectionLastUsedInMs = connectionLastUsed;
    }
    
    public long getConnectionLastResetInMs() {
        return this.connectionLastResetInMs;
    }
    
    @Deprecated
    public long getConnectionLastReset() {
        return this.getConnectionLastResetInMs();
    }
    
    protected void setConnectionLastResetInMs(final long connectionLastReset) {
        this.connectionLastResetInMs = connectionLastReset;
    }
    
    public boolean isPossiblyBroken() {
        return this.possiblyBroken;
    }
    
    public ConnectionPartition getOriginatingPartition() {
        return this.originatingPartition;
    }
    
    protected void setOriginatingPartition(final ConnectionPartition originatingPartition) {
        this.originatingPartition = originatingPartition;
    }
    
    protected void renewConnection() {
        this.logicallyClosed.set(false);
        this.threadUsingConnection = Thread.currentThread();
        if (this.doubleCloseCheck) {
            this.doubleCloseException = null;
        }
    }
    
    protected void clearStatementCaches(final boolean internalClose) {
        if (this.statementCachingEnabled) {
            if (internalClose) {
                this.callableStatementCache.clear();
                this.preparedStatementCache.clear();
            }
            else if (this.pool.closeConnectionWatch) {
                this.callableStatementCache.checkForProperClosure();
                this.preparedStatementCache.checkForProperClosure();
            }
        }
    }
    
    public Object getDebugHandle() {
        return this.debugHandle;
    }
    
    public void setDebugHandle(final Object debugHandle) {
        this.debugHandle = debugHandle;
    }
    
    @Deprecated
    public Connection getRawConnection() {
        return this.getInternalConnection();
    }
    
    public Connection getInternalConnection() {
        return this.connection;
    }
    
    public ConnectionHook getConnectionHook() {
        return this.connectionHook;
    }
    
    public boolean isLogStatementsEnabled() {
        return this.logStatementsEnabled;
    }
    
    public void setLogStatementsEnabled(final boolean logStatementsEnabled) {
        this.logStatementsEnabled = logStatementsEnabled;
    }
    
    protected boolean isInReplayMode() {
        return this.inReplayMode;
    }
    
    protected void setInReplayMode(final boolean inReplayMode) {
        this.inReplayMode = inReplayMode;
    }
    
    public boolean isConnectionAlive() {
        return this.pool.isConnectionHandleAlive(this);
    }
    
    public void setInternalConnection(final Connection rawConnection) {
        this.connection = rawConnection;
    }
    
    public BoneCP getPool() {
        return this.pool;
    }
    
    public List<ReplayLog> getReplayLog() {
        return this.replayLog;
    }
    
    protected void setReplayLog(final List<ReplayLog> replayLog) {
        this.replayLog = replayLog;
    }
    
    public Object getProxyTarget() {
        try {
            return Proxy.getInvocationHandler(this.connection).invoke(null, this.getClass().getMethod("getProxyTarget", (Class<?>[])new Class[0]), null);
        }
        catch (Throwable t) {
            throw new RuntimeException("BoneCP: Internal error - transaction replay log is not turned on?", t);
        }
    }
    
    public Thread getThreadUsingConnection() {
        return this.threadUsingConnection;
    }
    
    @Deprecated
    public long getConnectionCreationTime() {
        return this.getConnectionCreationTimeInMs();
    }
    
    public long getConnectionCreationTimeInMs() {
        return this.connectionCreationTimeInMs;
    }
    
    public boolean isExpired() {
        return this.maxConnectionAgeInMs > 0L && this.isExpired(System.currentTimeMillis());
    }
    
    protected boolean isExpired(final long currentTime) {
        return this.maxConnectionAgeInMs > 0L && currentTime - this.connectionCreationTimeInMs > this.maxConnectionAgeInMs;
    }
    
    protected void setThreadWatch(final Thread threadWatch) {
        this.threadWatch = threadWatch;
    }
    
    public Thread getThreadWatch() {
        return this.threadWatch;
    }
    
    protected boolean isTxResolved() {
        return this.txResolved;
    }
    
    protected String getAutoCommitStackTrace() {
        return this.autoCommitStackTrace;
    }
    
    protected void setAutoCommitStackTrace(final String autoCommitStackTrace) {
        this.autoCommitStackTrace = autoCommitStackTrace;
    }
    
    public void refreshConnection() throws SQLException {
        this.connection.close();
        try {
            this.connection = this.pool.obtainRawInternalConnection();
        }
        catch (SQLException e) {
            throw this.markPossiblyBroken(e);
        }
    }
    
    protected void untrackStatement(final StatementHandle statement) {
        if (this.closeOpenStatements) {
            this.trackedStatement.remove(statement);
        }
    }
    
    public String getUrl() {
        return this.url;
    }
    
    @Override
    public String toString() {
        final long timeMillis = System.currentTimeMillis();
        return Objects.toStringHelper(this).add("url", this.pool.getConfig().getJdbcUrl()).add("user", this.pool.getConfig().getUsername()).add("debugHandle", this.debugHandle).add("lastResetAgoInSec", TimeUnit.MILLISECONDS.toSeconds(timeMillis - this.connectionLastResetInMs)).add("lastUsedAgoInSec", TimeUnit.MILLISECONDS.toSeconds(timeMillis - this.connectionLastUsedInMs)).add("creationTimeAgoInSec", TimeUnit.MILLISECONDS.toSeconds(timeMillis - this.connectionCreationTimeInMs)).toString();
    }
    
    static {
        ConnectionHandle.logger = LoggerFactory.getLogger(ConnectionHandle.class);
        sqlStateDBFailureCodes = ImmutableSet.of("08001", "08006", "08007", "08S01", "57P01", "HY000", new String[0]);
    }
}
