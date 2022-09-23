// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import org.slf4j.LoggerFactory;
import java.sql.SQLWarning;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.Map;
import com.jolbox.bonecp.hooks.ConnectionHook;
import org.slf4j.Logger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.sql.Statement;

public class StatementHandle implements Statement
{
    protected AtomicBoolean logicallyClosed;
    protected Statement internalStatement;
    protected String sql;
    protected IStatementCache cache;
    protected ConnectionHandle connectionHandle;
    private String cacheKey;
    protected boolean logStatementsEnabled;
    public volatile boolean inCache;
    public String openStackTrace;
    private static final Logger logger;
    protected long queryExecuteTimeLimit;
    protected ConnectionHook connectionHook;
    private Object debugHandle;
    private boolean statisticsEnabled;
    private Statistics statistics;
    protected Map<Object, Object> logParams;
    protected StringBuilder batchSQL;
    
    public StatementHandle(final Statement internalStatement, final String sql, final IStatementCache cache, final ConnectionHandle connectionHandle, final String cacheKey, final boolean logStatementsEnabled) {
        this.logicallyClosed = new AtomicBoolean();
        this.inCache = false;
        this.logParams = new TreeMap<Object, Object>();
        this.batchSQL = new StringBuilder();
        this.sql = sql;
        this.internalStatement = internalStatement;
        this.cache = cache;
        this.cacheKey = cacheKey;
        this.connectionHandle = connectionHandle;
        this.logStatementsEnabled = logStatementsEnabled;
        final BoneCPConfig config = connectionHandle.getPool().getConfig();
        this.connectionHook = config.getConnectionHook();
        this.statistics = connectionHandle.getPool().getStatistics();
        this.statisticsEnabled = config.isStatisticsEnabled();
        try {
            this.queryExecuteTimeLimit = connectionHandle.getOriginatingPartition().getQueryExecuteTimeLimitinNanoSeconds();
        }
        catch (Exception e) {
            this.queryExecuteTimeLimit = 0L;
        }
        if (this.cache != null) {
            this.cache.putIfAbsent(this.cacheKey, this);
        }
    }
    
    public StatementHandle(final Statement internalStatement, final ConnectionHandle connectionHandle, final boolean logStatementsEnabled) {
        this(internalStatement, null, null, connectionHandle, null, logStatementsEnabled);
    }
    
    public void close() throws SQLException {
        this.connectionHandle.untrackStatement(this);
        this.logicallyClosed.set(true);
        if (this.logStatementsEnabled) {
            this.logParams.clear();
            this.batchSQL = new StringBuilder();
        }
        if (this.cache == null || !this.inCache) {
            this.internalStatement.close();
        }
    }
    
    public void addBatch(final String sql) throws SQLException {
        this.checkClosed();
        try {
            if (this.logStatementsEnabled) {
                this.batchSQL.append(sql);
            }
            this.internalStatement.addBatch(sql);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    protected void checkClosed() throws SQLException {
        if (this.logicallyClosed.get()) {
            throw new SQLException("Statement is closed");
        }
    }
    
    public void cancel() throws SQLException {
        this.checkClosed();
        try {
            this.internalStatement.cancel();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void clearBatch() throws SQLException {
        this.checkClosed();
        try {
            if (this.logStatementsEnabled) {
                this.batchSQL = new StringBuilder();
            }
            this.internalStatement.clearBatch();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void clearWarnings() throws SQLException {
        this.checkClosed();
        try {
            this.internalStatement.clearWarnings();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public boolean execute(final String sql) throws SQLException {
        boolean result = false;
        this.checkClosed();
        try {
            if (this.logStatementsEnabled && StatementHandle.logger.isDebugEnabled()) {
                StatementHandle.logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
            }
            final long timer = this.queryTimerStart();
            if (this.connectionHook != null) {
                this.connectionHook.onBeforeStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            result = this.internalStatement.execute(sql);
            if (this.connectionHook != null) {
                this.connectionHook.onAfterStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            this.queryTimerEnd(sql, timer);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    protected void queryTimerEnd(final String sql, final long queryStartTime) {
        if (this.queryExecuteTimeLimit != 0L && this.connectionHook != null) {
            final long timeElapsed = System.nanoTime() - queryStartTime;
            if (timeElapsed > this.queryExecuteTimeLimit) {
                this.connectionHook.onQueryExecuteTimeLimitExceeded(this.connectionHandle, this, sql, this.logParams, timeElapsed);
            }
        }
        if (this.statisticsEnabled) {
            this.statistics.incrementStatementsExecuted();
            this.statistics.addStatementExecuteTime(System.nanoTime() - queryStartTime);
        }
    }
    
    public boolean execute(final String sql, final int autoGeneratedKeys) throws SQLException {
        boolean result = false;
        this.checkClosed();
        try {
            if (this.logStatementsEnabled && StatementHandle.logger.isDebugEnabled()) {
                StatementHandle.logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
            }
            final long queryStartTime = this.queryTimerStart();
            if (this.connectionHook != null) {
                this.connectionHook.onBeforeStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            result = this.internalStatement.execute(sql, autoGeneratedKeys);
            if (this.connectionHook != null) {
                this.connectionHook.onAfterStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            this.queryTimerEnd(sql, queryStartTime);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    protected long queryTimerStart() {
        return (this.statisticsEnabled || (this.queryExecuteTimeLimit != 0L && this.connectionHook != null)) ? System.nanoTime() : Long.MAX_VALUE;
    }
    
    public boolean execute(final String sql, final int[] columnIndexes) throws SQLException {
        boolean result = false;
        this.checkClosed();
        try {
            if (this.logStatementsEnabled && StatementHandle.logger.isDebugEnabled()) {
                StatementHandle.logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
            }
            final long queryStartTime = this.queryTimerStart();
            if (this.connectionHook != null) {
                this.connectionHook.onBeforeStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            result = this.internalStatement.execute(sql, columnIndexes);
            if (this.connectionHook != null) {
                this.connectionHook.onAfterStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            this.queryTimerEnd(sql, queryStartTime);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public boolean execute(final String sql, final String[] columnNames) throws SQLException {
        boolean result = false;
        this.checkClosed();
        try {
            if (this.logStatementsEnabled && StatementHandle.logger.isDebugEnabled()) {
                StatementHandle.logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
            }
            final long queryStartTime = this.queryTimerStart();
            if (this.connectionHook != null) {
                this.connectionHook.onBeforeStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            result = this.internalStatement.execute(sql, columnNames);
            if (this.connectionHook != null) {
                this.connectionHook.onAfterStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            this.queryTimerEnd(sql, queryStartTime);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int[] executeBatch() throws SQLException {
        int[] result = null;
        this.checkClosed();
        try {
            if (this.logStatementsEnabled && StatementHandle.logger.isDebugEnabled()) {
                StatementHandle.logger.debug(PoolUtil.fillLogParams(this.batchSQL.toString(), this.logParams));
            }
            final long queryStartTime = this.queryTimerStart();
            String query = "";
            if (this.connectionHook != null) {
                query = this.batchSQL.toString();
                this.connectionHook.onBeforeStatementExecute(this.connectionHandle, this, query, this.logParams);
            }
            result = this.internalStatement.executeBatch();
            if (this.connectionHook != null) {
                this.connectionHook.onAfterStatementExecute(this.connectionHandle, this, query, this.logParams);
            }
            this.queryTimerEnd(this.logStatementsEnabled ? this.batchSQL.toString() : "", queryStartTime);
            if (this.logStatementsEnabled) {
                this.logParams.clear();
                this.batchSQL = new StringBuilder();
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public ResultSet executeQuery(final String sql) throws SQLException {
        ResultSet result = null;
        this.checkClosed();
        try {
            if (this.logStatementsEnabled && StatementHandle.logger.isDebugEnabled()) {
                StatementHandle.logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
            }
            final long queryStartTime = this.queryTimerStart();
            if (this.connectionHook != null) {
                this.connectionHook.onBeforeStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            result = this.internalStatement.executeQuery(sql);
            if (this.connectionHook != null) {
                this.connectionHook.onAfterStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            this.queryTimerEnd(sql, queryStartTime);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int executeUpdate(final String sql) throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            if (this.logStatementsEnabled && StatementHandle.logger.isDebugEnabled()) {
                StatementHandle.logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
            }
            final long queryStartTime = this.queryTimerStart();
            if (this.connectionHook != null) {
                this.connectionHook.onBeforeStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            result = this.internalStatement.executeUpdate(sql);
            if (this.connectionHook != null) {
                this.connectionHook.onAfterStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            this.queryTimerEnd(sql, queryStartTime);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int executeUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            if (this.logStatementsEnabled && StatementHandle.logger.isDebugEnabled()) {
                StatementHandle.logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
            }
            final long queryStartTime = this.queryTimerStart();
            if (this.connectionHook != null) {
                this.connectionHook.onBeforeStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            result = this.internalStatement.executeUpdate(sql, autoGeneratedKeys);
            if (this.connectionHook != null) {
                this.connectionHook.onAfterStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            this.queryTimerEnd(sql, queryStartTime);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int executeUpdate(final String sql, final int[] columnIndexes) throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            if (this.logStatementsEnabled && StatementHandle.logger.isDebugEnabled()) {
                StatementHandle.logger.debug(PoolUtil.fillLogParams(sql, this.logParams), columnIndexes);
            }
            final long queryStartTime = this.queryTimerStart();
            if (this.connectionHook != null) {
                this.connectionHook.onBeforeStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            result = this.internalStatement.executeUpdate(sql, columnIndexes);
            if (this.connectionHook != null) {
                this.connectionHook.onAfterStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            this.queryTimerEnd(sql, queryStartTime);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int executeUpdate(final String sql, final String[] columnNames) throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            if (this.logStatementsEnabled && StatementHandle.logger.isDebugEnabled()) {
                StatementHandle.logger.debug(PoolUtil.fillLogParams(sql, this.logParams));
            }
            final long queryStartTime = this.queryTimerStart();
            if (this.connectionHook != null) {
                this.connectionHook.onBeforeStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            result = this.internalStatement.executeUpdate(sql, columnNames);
            if (this.connectionHook != null) {
                this.connectionHook.onAfterStatementExecute(this.connectionHandle, this, sql, this.logParams);
            }
            this.queryTimerEnd(sql, queryStartTime);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public Connection getConnection() throws SQLException {
        this.checkClosed();
        return this.connectionHandle;
    }
    
    public int getFetchDirection() throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            result = this.internalStatement.getFetchDirection();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int getFetchSize() throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            result = this.internalStatement.getFetchSize();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public ResultSet getGeneratedKeys() throws SQLException {
        ResultSet result = null;
        this.checkClosed();
        try {
            result = this.internalStatement.getGeneratedKeys();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int getMaxFieldSize() throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            result = this.internalStatement.getMaxFieldSize();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int getMaxRows() throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            result = this.internalStatement.getMaxRows();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public boolean getMoreResults() throws SQLException {
        boolean result = false;
        this.checkClosed();
        try {
            result = this.internalStatement.getMoreResults();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public boolean getMoreResults(final int current) throws SQLException {
        boolean result = false;
        this.checkClosed();
        try {
            result = this.internalStatement.getMoreResults(current);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int getQueryTimeout() throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            result = this.internalStatement.getQueryTimeout();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public ResultSet getResultSet() throws SQLException {
        ResultSet result = null;
        this.checkClosed();
        try {
            result = this.internalStatement.getResultSet();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int getResultSetConcurrency() throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            result = this.internalStatement.getResultSetConcurrency();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int getResultSetHoldability() throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            result = this.internalStatement.getResultSetHoldability();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int getResultSetType() throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            result = this.internalStatement.getResultSetType();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public int getUpdateCount() throws SQLException {
        int result = 0;
        this.checkClosed();
        try {
            result = this.internalStatement.getUpdateCount();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public SQLWarning getWarnings() throws SQLException {
        SQLWarning result = null;
        this.checkClosed();
        try {
            result = this.internalStatement.getWarnings();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public boolean isClosed() {
        return this.logicallyClosed.get();
    }
    
    public void setPoolable(final boolean poolable) throws SQLException {
        this.checkClosed();
        try {
            this.internalStatement.setPoolable(poolable);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        boolean result = false;
        try {
            result = this.internalStatement.isWrapperFor(iface);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        T result = null;
        try {
            result = this.internalStatement.unwrap(iface);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public boolean isPoolable() throws SQLException {
        boolean result = false;
        this.checkClosed();
        try {
            result = this.internalStatement.isPoolable();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
        return result;
    }
    
    public void closeOnCompletion() throws SQLException {
        this.internalStatement.closeOnCompletion();
    }
    
    public boolean isCloseOnCompletion() throws SQLException {
        return this.internalStatement.isCloseOnCompletion();
    }
    
    public void setCursorName(final String name) throws SQLException {
        this.checkClosed();
        try {
            this.internalStatement.setCursorName(name);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setEscapeProcessing(final boolean enable) throws SQLException {
        this.checkClosed();
        try {
            this.internalStatement.setEscapeProcessing(enable);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setFetchDirection(final int direction) throws SQLException {
        this.checkClosed();
        try {
            this.internalStatement.setFetchDirection(direction);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setFetchSize(final int rows) throws SQLException {
        this.checkClosed();
        try {
            this.internalStatement.setFetchSize(rows);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setMaxFieldSize(final int max) throws SQLException {
        this.checkClosed();
        try {
            this.internalStatement.setMaxFieldSize(max);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setMaxRows(final int max) throws SQLException {
        this.checkClosed();
        try {
            this.internalStatement.setMaxRows(max);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setQueryTimeout(final int seconds) throws SQLException {
        this.checkClosed();
        try {
            this.internalStatement.setQueryTimeout(seconds);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    protected void clearCache() {
        if (this.cache != null) {
            this.cache.clear();
        }
    }
    
    protected void setLogicallyOpen() {
        this.logicallyClosed.set(false);
    }
    
    @Override
    public String toString() {
        return this.sql;
    }
    
    public String getOpenStackTrace() {
        return this.openStackTrace;
    }
    
    public void setOpenStackTrace(final String openStackTrace) {
        this.openStackTrace = openStackTrace;
    }
    
    public Statement getInternalStatement() {
        return this.internalStatement;
    }
    
    public void setInternalStatement(final Statement internalStatement) {
        this.internalStatement = internalStatement;
    }
    
    public void setDebugHandle(final Object debugHandle) {
        this.debugHandle = debugHandle;
    }
    
    public Object getDebugHandle() {
        return this.debugHandle;
    }
    
    static {
        logger = LoggerFactory.getLogger(StatementHandle.class);
    }
}
